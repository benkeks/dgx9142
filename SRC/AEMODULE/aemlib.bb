;.AEM-Lib
;AEMs beschreiben unsere Entities sowie Animationssequenzen.

;Auf den AEMSyntax können auch AFX- und Leveldateien zugreifen.

Const aem_version$ = "0.2.1"

Type aemSeq
	Field name$
	Field mesh
	Field no
End Type

Type aem_tex
	Field tex
End Type

Type aem_brush
	Field brush
	Field name$
End Type

Global AEM_ReturnDot3=0


Function LoadMeshA(path$, parent=0)
	AEM_ReturnDot3	= 0
	Select Right(path,3)
	Case "aem"
		Return AEM_Load(path,parent)
	Case "afx"
		Return FX_LoadAFX(path,parent)
	Case "jpg","png","bmp"
		Return LoadSprite(path,1+2,parent)
	Default
		Return LoadMesh(path,parent)
	End Select
End Function

Function AEM_Clear()
	For t.aem_tex = Each aem_tex
		FreeTexture t\tex
		Delete t.aem_tex
	Next
End Function

Function AEM_Load(path$,parent=0)
	Util_CheckFile(path$)
	Stream = ReadFile(path$)
	Return AEM_Parse(stream,0,parent)
End Function

Function AEM_FreeAEM(entity)
	FreeEntity entity
	AEM_FreeSeq(entity)
End Function

Function AEM_Parse(stream,mesh=0,parent=0)


	Repeat
		lin$ = util_trim2(Lower(ReadLine(stream)))
		
		Select lin
		Case "version"
			version$ = ReadLine(stream)
			If version <> aem_version
				DebugLog "warning: different version!"
			EndIf
		Case "texfilter{"
			AEM_Parsefilter(stream)
		Case "piv{}"
			mesh = CreatePivot(parent)
		Case "skyelement{}"
			EntityParent mesh,env_sky
		Case "mesh{"
			mesh = AEM_Parsemesh(stream,parent)
		Case "terrain{"
			mesh = AEM_ParseTerrain(stream,parent)
		Case "entity{"
			AEM_ParseEntity(stream,mesh)
		Case "defbrush{"
			AEM_ParseBrushDef(stream)
		Case "paint{"
			AEM_ParsePaint(stream,mesh)
		Case "material{"
			AEM_ParseMaterial(Stream,mesh)
		Case "water{"
			mesh = AEM_ParseWater(stream,mesh)
		Case "texture{"
			AEM_ParseTexture(stream,mesh)
		Case "animate{"
			AEM_ParseAnimate(stream,mesh)
		Case "sequence{"
			AEM_ParseSequence(stream,mesh)
		Case "collision{"
			AEM_ParseCollision(stream,mesh)
		Case "shader{"
			AEM_ParseShader(stream,mesh)
		Case "child{"
			AEM_Parse(stream,0,mesh)
		Case "object{"
			Map_ParseObject(stream,mesh)
			
		Case "afx{"
			mesh = FX_ParseAFX(stream,parent)
		Case "error"
			RuntimeError "AEM Debug-Error"
		Default
			If Util_GetParas(lin)
				Select Lower(paras[0])
				Case "fromfile"
					stream2 = ReadFile(paras[1])
					mesh = AEM_Parse(stream2,mesh) 
				End Select
			EndIf
			If Left(Util_Trim2(Lower(lin)),1) = "<"
				Main_ParseDetail(stream, lin)
			EndIf
		End Select
		
	Until lin = "}" Or Eof(stream)

	ClearTextureFilters

	Return mesh
End Function

Function AEM_ParseFilter(stream)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "on"
				on$ = paras[1]
			Case "mode"
				mode = paras[1]
			End Select
		EndIf
	Until lin="}"
	TextureFilter on$,mode
End Function

Function AEM_ParseMesh(stream,parent=0)
	Local sx#=1,sy#=1,sz#=1
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "path","file","mesh"
				path$ = paras[1]
			Case "anim"
				anim = paras[1]
			Case "x"
				x#	= paras[1]
			Case "y"
				y#	= paras[1]
			Case "z"
				z#	= paras[1]
			Case "scalex","sx"
				sx#	= paras[1]
			Case "scaley","sy"
				sy#	= paras[1]
			Case "scalez","sz"
				sz#	= paras[1]
			Case "pitch"
				pit#= paras[1]
			Case "yaw"
				yaw#= paras[1]
			Case "roll"
				roll#= paras[1]
			Case "flip"
				Flipm = 1
			Case "renormalmap"
				renormalmap = 1
			End Select
		EndIf
	Until lin="}"
	
	If anim Then
		mesh = LoadAnimMesh(path,parent)
	Else
		mesh = LoadMesh(path,parent)
	EndIf
	
	ScaleMesh mesh,sx,sy,sz
	RotateMesh mesh,pit,yaw,roll
	PositionMesh mesh,x,y,z
	If flipm Then FlipMesh mesh
	If renormalmap Then UpdateNormals mesh
	
	Return mesh
End Function

Function AEM_ParseWater(stream,parent=0)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "size"
				size	= paras[1]
			Case "rad"
				rad		= paras[1]
			End Select
		EndIf
	Until lin="}"
	water.wat = Wat_CreateWater(size,size)
	mesh = Wat_CreateSurface(water.wat,rad)
	
	Return mesh
End Function

Function AEM_ParseTerrain(stream,parent=0)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "hmap"
				mesh = LoadTerrain(paras[1],parent)
			Case "detail"
				TerrainDetail mesh,paras[1],1
			Case "shading"
				TerrainShading mesh,paras[1] 
			End Select
		EndIf
	Until lin="}"
	
	cc_gridmode = 0
	
	Return mesh
End Function

Function AEM_ParseEntity(stream,mesh)
	Local sx# = 1, sy# = 1, sz# = 1
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "x","px"
				x#	= paras[1]
			Case "y","py"
				y#	= paras[1]
			Case "z","pz"
				z#	= paras[1]
			Case "scalex","sx"
				sx#	= paras[1]
			Case "scaley","sy"
				sy#	= paras[1]
			Case "scalez","sz"
				sz#	= paras[1]
			Case "pitch","rx"
				pit#= paras[1]
			Case "yaw","ry"
				yaw#= paras[1]
			Case "roll","rz"
				roll#= paras[1]
			Case "name"
				NameEntity mesh,paras[1]
			Case "pointtoparent"
				ptp	 = paras[1]
			Case "pointtocam"
				ptc = paras[1]
			End Select
		EndIf
	Until lin="}"

	ScaleEntity mesh,sx,sy,sz
	RotateEntity mesh,pit,yaw,roll
	PositionEntity mesh,x,y,z
	If ptp Then PointEntity mesh,GetParent(mesh)
	If ptc Then PointEntity mesh,cam_cam

	o.obj = Obj_GetObject(mesh)
	If o <> Null Then
		o\px	= x#
		o\py	= y#
		o\pz	= z#
		o\rx	= Pit#
		o\ry	= yaw#
		o\rz	= roll#
	EndIf
	
End Function


Function AEM_ParseBrushDef(stream)	
	Local r = 255, g = 255, b = 255
	Local alpha = 1, fx = 1, blend = 1
	
	blend = 1
	mode = 1
	layer = 0
	sx#	  = 1
	sy#	  = 1
	
	brush = CreateBrush()
	mesh = brush
	
	br.aem_Brush = New aem_brush
	br\brush = mesh
	
	Repeat
		lin$ = util_Trim2(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "name"
				br\name$	= paras[1]
			Case "alpha"
				BrushAlpha mesh,paras[1]
			Case "shininess"
				BrushShininess mesh,paras[1]
			Case "fx"
				BrushFX mesh,paras[1]
			Case "blend"
				BrushBlend mesh,paras[1]
			Case "red"
				r = paras[1]
			Case "green"
				g = paras[1]
			Case "blue"
				b = paras[1]
			;Case "materialclass"
			;	o\materialClass	= paras[1]
			End Select
		Else
			Select lin
			Case "texture{"
				tex = AEM_ParseTexture(stream,brush,1)
				BrushTexture mesh,tex
			End Select
		EndIf
	Until lin="}"
	If r+g+b Then BrushColor mesh,r,g,b
End Function

Function AEM_ParsePaint(stream,mesh)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "name"
				brush = AEM_FindBrush(paras[1])
			End Select
		EndIf
	Until lin="}"
	PaintMesh mesh,brush
End Function

Function AEM_ParseMaterial(stream,mesh)
	Local r = 255, g = 255, b = 255
	Local alpha = 1, fx = 1, blend = 1
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "alpha"
				EntityAlpha mesh,paras[1]
			Case "shininess"
				EntityShininess mesh,paras[1]
			Case "fx"
				EntityFX mesh,paras[1]
			Case "blend"
				EntityBlend mesh,paras[1]
			Case "order"
				EntityOrder mesh,paras[1]
			Case "red"
				r = paras[1]
			Case "green"
				g = paras[1]
			Case "blue"
				b = paras[1]
			;Case "materialclass"
			;	o\materialClass	= paras[1]
			End Select
		EndIf
	Until lin="}"
	EntityColor mesh,r,g,b
End Function

Function AEM_ParseTexture(stream,mesh,bru = 0)
	blend = 1
	mode = 1+8
	layer = 0
	sx#	  = 1
	sy#	  = 1


	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "path","file"
				path$ = paras[1]
			Case "mode"
				mode = paras[1]
			Case "x"
				x#	= paras[1]
			Case "y"
				y#	= paras[1]
			Case "scalex"
				sx#	= paras[1]
			Case "scaley"
				sy#	= paras[1]
			Case "roll"
				roll#= paras[1]
			Case "coords"
				coords= paras[1]
			Case "blend"
				blend = paras[1]
			Case "layer"
				layer = paras[1]
			Case "cubemode"
				cubemode = paras[1]
			End Select
		EndIf
	Until lin="}"
	
	If path = "normal"
		If bru = 0 Then
			EntityTexture mesh,env_normal,0,layer
		Else
			BrushTexture mesh,env_normal,0,layer
		EndIf
	ElseIf path = "normaldif"
		If bru = 0 Then
			EntityTexture mesh,env_normaldif,0,layer
		Else
			BrushTexture mesh,env_normaldif,0,layer
		EndIf
	ElseIf path = "specadd"
		If bru = 0 Then
			EntityTexture mesh,shi_specular2additive,0,layer
		Else
			BrushTexture mesh,shi_specular2additive,0,layer
		EndIf
	Else
		If mode = 2 Then
			tex = LoadTexture(path$,2)
		Else
			tex = LoadTexture(path$,mode)
		EndIf
		PositionTexture tex,x,y
		ScaleTexture tex,sx,sy
		RotateTexture tex,roll
		
		TextureCoords tex,coords
		TextureBlend tex,blend
		
		If cubemode <> 0 Then SetCubeMode tex,cubemode
		
		If bru = 0 Then
			EntityTexture mesh,tex,0,layer
		Else
			BrushTexture mesh,tex,0,layer
		EndIf
		
		t.AEM_Tex= New AEM_tex
		t\tex	 = tex
	EndIf
	
	If blend=4 Then AEM_ReturnDot3 = 1
	
	Return tex
End Function

Function AEM_ParseAnimate(stream,mesh)
	mode	= 1
	speed#	= 1
	seq		= 0
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "mode"
				mode	= paras[1]
			Case "speed"
				speed#	= paras[1]
			Case "seq"
				seq		= paras[1]
			Case "trans"
				trans#	= paras[1]
			End Select
		EndIf
	Until lin="}"
	
	Animate mesh,mode,speed,seq,trans
End Function
 

Function AEM_ParseSequence(stream,mesh)

	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "name"
				name$	= paras[1]
			Case "start"
				start	= paras[1]
			Case "end"
				Ende	= paras[1]
			Case "frommesh"
				frommesh$=paras[1]
			End Select
		EndIf
	Until lin="}"
	If frommesh <> ""
		no = LoadAnimSeq(mesh,frommesh$)
	Else
		no = ExtractAnimSeq(mesh,start,ende)
	EndIf
	AEM_SaveSeq(name$,mesh,no)
	Return no
End Function

Function AEM_ParseCollision(stream,mesh)


	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "type"
				typ$ = paras[1]
				Select typ
				Case "map"
					typ = map_colli
				Case "forcefield"
					typ = map_forcefield
				End Select
				EntityType mesh,typ: DebugLog "typ"+typ
			Case "pickgeo"
				pickgeo = paras[1]
			Case "pickcover"
				pickcover = paras[1]

			; für Object{ - Objekte
			Case "method"		; box, radius
				method	= paras[1]
			Case "boxw"
				box_w = paras[1]
			Case "boxh"
				box_h = paras[1]
			Case "boxd"
				box_d = paras[1]
			Case "radius"
				radius	= paras[1]
				

			End Select
		EndIf
	Until lin="}"
	EntityPickMode mesh,pickgeo,pickcover

	o.obj = Obj_GetObject(mesh)
	If o <> Null Then
		Select method
		Case 1
			EntityRadius o\entity, radius
			o\collengine	= BLITZ3D_Collisions
		Case 0
			EntityBox o\entity, -boxw/2,-boxh/2,-boxd/2,boxw,boxh,boxd
			o\collengine	= BLITZ3D_Collisions
		Case 3
			EntityType mesh,0
		End Select
	EndIf
	
End Function


Function AEM_ParseShader(stream,mesh)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "name"
				name$	= paras[1]
			Case "updatetimer"
				uptimer = paras[1]
			Case "speed"
				speed#	= paras[1]
			End Select
		EndIf
	Until lin="}"
	Sha_AddShader(mesh,name,uptimer,speed)
End Function	


Function AEM_ParseDot3(stream,o.obj)
If o = Null Then DebugLog "AEM_ParseDot3():	o.obj doesn't exist! But Aliens Exist!!"
DebugLog "parsing dot3 ..."

;	o\Dot3su = 1
;	o\Dot3sv = 1
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(Util_Trim2(paras[0]))
;			Case "path"
;				o\Dot3map		= paras[1]
;			Case "static"
;				o\dot3Static	= paras[1]
;			Case "scaleu"
;				o\Dot3su		= paras[1]
;			Case "scalev"
;				o\Dot3sv		= paras[1]
			End Select
		EndIf
	Until lin="}"

;	If o\Dot3map Then
;		o\dot3 = True
;		o\Dot3Tex	= LoadTexture( o\Dot3map )
;		If o\Dot3Tex = 0 Then
;			DebugLog "AEM_ParseDot3(): dot3map '"+o\Dot3map+"' doesn't exist."
;			o\Dot3 = False
;			o\Dot3Map = "[error] "+o\Dot3Map
;		Else
;			TextureBlend o\Dot3Tex,4
;			ScaleTexture o\dot3tex, o\Dot3su, o\Dot3sv
;			EntityTexture o\entity, o\dot3tex, 0,TLAY_DOT3
;		EndIf
;	EndIf
;	Return True
End Function

Function AEM_ParseCubic(stream,o.obj)
If o = Null Then DebugLog "AEM_ParseCubic():	o.obj doesn't exist! But Aliens Exist!!"
DebugLog "parsing cubic ..."

;	o\Dot3su = 1
;	o\Dot3sv = 1
;	o\cubic	= True
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(Util_Trim2(paras[0]))
;			Case "staticmap"
;				o\cubicStaticMap$	= paras[1]
;			Case "size"
;				size				= paras[1]
;				Select size
;					Case 64:	o\cubicSize = 0
;					Case 128:	o\cubicSize = 1
;					Case 256:	o\cubicSize = 2
;					Case 512:	o\cubicSize = 3
;					Case 1024:o\cubicSize = 4
;					Default:	o\cubicSize = 2
;				End Select					
;			Case "rendermode"
;				o\cubicTyp		= paras[1]
;			Case "blendmode"
;				o\cubicBlend		= paras[1]
;			Case "cubemode"
;				o\cubicMode		= paras[1]
			End Select
		EndIf
	Until lin="}"

;	If o\cubicStaticMap Then
;		o\cubicTex	= LoadTexture( o\cubicStaticMap,128 )
;		
;		If o\cubicTex = 0 Then
;			DebugLog "AEM_ParseCubic(): static cubemap '"+o\cubicStaticMap+"' doesn't exist."
;			o\cubicStaticMap = "[error] "+o\cubicStaticMap
;		Else
;			TextureBlend o\cubicTex,o\cubicBlend
;			EntityTexture o\entity, o\cubicTex, 0,TLAY_CUBEMAP
;		EndIf
;	EndIf
;	Return True
End Function


Function AEM_SaveSeq(name$,mesh,no)
	s.aemSeq= New aemSeq
	s\name$	= name$
	s\mesh	= mesh
	s\no	= no
End Function

Function AEM_GetSeq(mesh,name$)
	For s.aemSeq = Each aemSeq
		If mesh = s\mesh And name$ = s\name$
			Return no
		EndIf
	Next
End Function

Function AEM_FreeSeq(mesh)
	For s.aemSeq = Each aemSeq
		If mesh = s\mesh
			Delete s.aemSeq
		EndIf
	Next
End Function

Function AEM_FindBrush(name$)
	For b.aem_brush = Each aem_brush
		If name$ = b\name$ Then Return b\brush
	Next
End Function

Function AEM_OvertakeAnim(mesh1,mesh2)
	children = CountChildren(mesh1)
	For c = 1 To children
		child = GetChild(mesh1,c)
		name$ = EntityName(child)
		If name <> "fire"
			child2= FindChild(mesh2,name)
			If child2
				AEM_OvertakeAnim(child,child2)
				;If name <> "start" And name <> "stomach"
				;	EntityParent child2,child
				;	PositionEntity child2,0,0,0
				;	RotateEntity child2,0,0,0
				;EndIf
				PositionEntity child2,EntityX(child),EntityY(child),EntityZ(child)
				RotateEntity child2,EntityPitch(child),EntityYaw(child),EntityRoll(child)
				
			EndIf
		EndIf
	Next
End Function

Function AEM_CopyAnimMesh(qmesh,par)
	mesh = CopyMesh(qmesh,par)
	For s.aemSeq = Each aemSeq
		If qmesh = s\mesh
			;seq = ExtractAnimSeq()
		EndIf
	Next
End Function