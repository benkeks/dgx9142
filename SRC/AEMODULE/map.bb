;Diese Include soll sich um das Laden und Anzeigen von Levels kümmern.
;Bitte vor alle Globalen und Funktionen das Präfix "Map_" setzen!

Const map_version = "1.2.1"

Global map_ent
Global map_name$
Global map_sun
Global map_atmo
Global map_gravi#

Global map_radius

; Physics-Variablen
Global map_gravityx#
Global map_gravityy#
Global map_gravityz#


; Levelstart
Global map_startx#
Global map_starty#
Global map_startz#
Global map_levstart

; Texte
Global map_task$
Global map_info$

Function Map_LoadMap(path$)
	map_ent = CreatePivot()
	
	Map_Load(path$)

	;Toklib_CalculateEnvir()
End Function


Function Map_Load(path$)
	Util_CheckFile(path$)
	Stream = ReadFile(path$)
	
	map_atmo = 0
	map_gravi = 0
	
	Return Map_Parse(stream, FileSize(path$))
End Function

	
Function Map_Parse(stream,size = 1)
	Repeat
		lin$ = Trim(ReadLine(stream))

		Select Util_Trim2(Lower(lin))
		Case "version"
			version$ = Trim(ReadLine(stream))
			If version <> map_version
				DebugLog "warning: different version!"
			EndIf
		Case "mapinfo{",main_lang+"mapinfo{"
			map_info = Map_ParseText(stream)
			HUD_SetText(map_info,hud_start,-200,100)
		Case "maptask{",main_lang+"maptask{"
			map_task = Map_ParseText(stream)
			HUD_SetText(map_task,hud_start,-200,-50)
		Case "team{"
			Team_Parse(stream)
		Case "ambient{"
			Map_ParseAmbient(stream)
		Case "dust{"
			Dust_ParseDust(stream)
		Case "vegetation{"
			Dust_ParseVegetation(stream)
		Case "levelstart{","lvlstart{"
			Map_ParseLevelstart(stream)
		Case "cluster{"
			Asteroids_ParseCluster(stream)
		Case "object{"
			Map_ParseObject(stream)
		Case "listener{"
			Map_ParseListener(stream)
		Case "sky{"
			Map_ParseSky(Stream)
		Case "sky2{"
			Map_ParseSky2(Stream)
		Case "sky3{"
			Map_ParseSky3(Stream)
		Case "sky4{"
			Map_ParseSky4(Stream)
		Case "spawn{"
			Fla_ParseSpawn(stream)
		Case "conquest{"
			Map_ParseConquest(stream)
		Case "script{"
			Prs_ParseScript(stream)
		Default
			If Util_GetParas(lin$)
				Select Lower(paras[0])
				Case "name",main_lang+"name"
					map_name = paras[1]
				Case "minimap"
					;HUD_SetMinimap paras[1]
				Case "mmspace","minimapspace"
					Hud_mspace = paras[1]
				Case "radius"
					map_radius = paras[1]
				Case "atmo"
					map_atmo = 1
				Case "gravi"
					map_gravi = paras[1]
				End Select
			ElseIf Left(Util_Trim2(Lower(lin)),1) = "<"
				Main_ParseDetail(stream, lin)
			EndIf
		End Select
		If net=1 And net_isserver=0 And MilliSecs() > ttime
			; hello server, i'm still here!
			WriteByte UDP_Stream,UDP_CheckPresence
			WriteByte UDP_Stream,UDP_ID
			AddUDPByte(C_End) : SendUDPData()
			ttime = MilliSecs()+1000
		EndIf
		SetBuffer FrontBuffer()
		Rect 0,GraphicsHeight()-5,GraphicsWidth()*FilePos(stream)/size,5
		SetBuffer BackBuffer()
	Until lin = "}" Or Eof(stream)
		
	If shi_specular = 0 Then
		shi_specular = LoadTexture("GFX/Environ/spec.png",1+128)
		TextureBlend shi_specular,3
	EndIf
	
	If shi_specular2 = 0 Then
		shi_specular2 = LoadTexture("GFX/Environ/spec2.png",1+128)
		SetCubeMode shi_specular2,2
		TextureBlend shi_specular2,5
	EndIf
	
	Return mesh
End Function

Function Map_ParseText$(stream)
	active = 1
	Repeat
		lin$ = Trim(ReadLine(stream))
		If lin="}" Then
			Return txt$
		Else
			If Left(lin,1) = "_"
				If Right(lin,Len(lin)-1) = main_lang Then active = 1 Else active = 0
			ElseIf active = 1 Then
				txt$ = txt$ + lin + Chr(13)
			EndIf
			
		EndIf
	Until lin="}"
End Function

Function Map_ParseSky(stream)

	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "file","path"
					source$	=	paras[1]
			End Select
		EndIf
	Until lin="}"

	tex = LoadTexture( source$,128 )
	SetCubeMode tex,3
	cam_skysprite	= CreateSprite(env_sky)
	EntityTexture cam_skysprite, tex
	;MoveEntity cam_skysprite,0,0,100
	EntityOrder cam_skysprite, 10
	EntityFX cam_skysprite,1+8
	ScaleSprite cam_skysprite, 200,200
	
	map_sky = CreatePivot()
	env_skymode = 1
	
End Function

Function Map_ParseSky2(stream)

	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "file","path"
					source$	=	paras[1]
			End Select
		EndIf
	Until lin="}"

	tex = LoadTexture( source$ )
	map_sky = CreateCube(env_sky)
	ScaleMesh map_sky,200,200,200
	EntityOrder map_sky,10
	EntityFX map_sky,1+8
	FlipMesh map_sky
	;EntityColor map_sky,0,0,0
	EntityTexture map_sky,tex
	
	env_skymode = 2
End Function

Function Map_ParseSky3(stream)

	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "file","path"
					source$	=	paras[1]
			End Select
		EndIf
	Until lin="}"

	tex = LoadTexture( source$ )
	
	map_sky=CreateMesh(env_sky)
	m = map_sky
	env_skymode = 3
	
	;front face
	b=LoadBrush( source+"_f.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
	AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;right face
	b=LoadBrush( source+"_l.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
	AddVertex s,+1,-1,+1,1,1:AddVertex s,+1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;back face
	b=LoadBrush( source+"_b.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
	AddVertex s,-1,-1,+1,1,1:AddVertex s,+1,-1,+1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;left face
	b=LoadBrush( source+"_r.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
	AddVertex s,-1,-1,-1,1,1:AddVertex s,-1,-1,+1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;top face
	b=LoadBrush( source+"_u.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
	AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;bottom face	; bottom shouldnt be visible due to the water ^^
	;b=LoadBrush( "sky_d.bmp",49 )
	;s=CreateSurface( m,b )
	;AddVertex s,-1,-1,-1,1,0:AddVertex s,+1,-1,-1,1,1
	;AddVertex s,+1,-1,+1,0,1:AddVertex s,-1,-1,+1,0,0
	;AddTriangle s,0,1,2:AddTriangle s,0,2,3
	;FreeBrush b
	ScaleMesh map_sky,300,300,300
	EntityOrder map_sky,10
	EntityFX map_sky,1+8
	FlipMesh map_sky
End Function

Function Map_ParseSky4(stream)

	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "file","path"
					source$	=	paras[1]
			End Select
		EndIf
	Until lin="}"
	
	env_skymode = 4
	map_sky = LoadMesh(source,env_sky)
	EntityOrder map_sky,10
	EntityFX map_sky,1+8
End Function


Function Map_ParseAmbient(stream)
	spec1$ = "GFX/Environ/spec.png"
	spec2$ = "GFX/Environ/spec2.png"
	normal$= "GFX/Environ/normalpixels.png"
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "ambr"
				ambr	= paras[1]
			Case "ambg"
				ambg	= paras[1]
			Case "ambb"
				ambb	= paras[1]
			Case "spec1", "diffuse"
				spec1$	= paras[1]
			Case "spec2", "specular"
				spec2$	= paras[1]
			Case "normal"
				normal$ = paras[1]
			Case "fogmode"
				CameraFogMode cam_cam,paras[1]
			Case "fograngemin"
				frmin	= paras[1]
			Case "fograngemax"
				frmax	= paras[1]
			Case "fogr"
				fogr	= paras[1]
			Case "fogg"
				fogg	= paras[1]
			Case "fogb"
				fogb	= paras[1]
			Case "cammode"
				CameraClsMode cam_cam,paras[1],1
			Case "camrangemin"
				crmin#	= paras[1]
			Case "camrangemax"
				crmax#	= paras[1]
			Case "camcr"
				camr	= paras[1]
			Case "camcg"
				camg	= paras[1]
			Case "camcb"
				camb	= paras[1]
			Case "bloom"
				bloom_effect = paras[1]
			Case "darkening"
				bloom_effect2 = paras[1]
			End Select
		EndIf
	Until lin="}"
	
	AmbientLight ambr,ambg,ambb
	
	CameraRange cam_cam,crmin,crmax
	cc_range1 = crmin
	cc_range2 = crmax
	CameraClsColor cam_cam,camr,camg,camb
	
	CameraFogRange cam_cam,frmin,frmax
	CameraFogColor cam_cam,fogr,fogg,fogb 
	
	shi_specular = LoadTexture(spec1,1+128)
	TextureBlend shi_specular,3
	
	shi_specular2 = LoadTexture(spec2,1+128)
	SetCubeMode shi_specular2,2
	TextureBlend shi_specular2,5
	
	shi_specular2additive = LoadTexture(spec2,1+128)
	For i = 0 To 5
		SetCubeFace shi_specular2additive,i
		LockBuffer TextureBuffer(shi_specular2additive)
		For x = 0 To TextureWidth(shi_specular2additive)-1
			For y = 0 To TextureWidth(shi_specular2additive)-1
				rgb	= ReadPixelFast(x,y,TextureBuffer(shi_specular2additive))
				a = (rgb And $FF000000)/$1000000
				r = (rgb And $FF0000)/$10000
				g = (rgb And $FF00)/$100
				b = rgb And $FF 
				rgb = a*$1000000 + (r/2)*$10000 + (g/2)*$100 + (b/2)
				WritePixelFast(x,y,rgb,TextureBuffer(shi_specular2additive))
			Next
		Next
		UnlockBuffer TextureBuffer(shi_specular2additive)
	Next
	SetCubeMode shi_specular2additive,2
	TextureBlend shi_specular2additive,3
	
	env_normal	= LoadTexture(normal,1+128)
	SetCubeMode env_normal,1
	
	env_normaldif	= LoadTexture(normal,1+128)
	SetCubeMode env_normaldif,2
End Function

Function Map_ParseLevelstart(Stream)

	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "x","px"
					x#	=	paras[1]
				Case "y","py"
					y#	=	paras[1]
				Case "z","pz"
					z#	=	paras[1]
				Case "pitch"
					pitch#	= paras[1]
				Case "yaw"
					yaw#	= paras[1]
				Case "roll"
					roll#	= paras[1]
			End Select
		EndIf
	Until lin="}"

	map_startx = x#
	map_starty = y#
	map_startz = z#
	
	map_levstart = CreatePivot()
	PositionEntity map_levstart,x,y,z
	RotateEntity map_levstart,pitch,yaw,roll
	CC_SetTarget(map_levstart,1,1)
	
	Return True
End Function

Function Map_ParseListener(stream)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "rolloff"
				rolloff#= paras[1]
			Case "doppler"
				doppler#	= paras[1]
			Case "dist","distance"
				dist#	= paras[1]
			End Select
		EndIf
	Until lin="}"
	
	cam_listener = CreateListener( cam_cam,rolloff,doppler,dist )
	DebugLog rolloff+" "+doppler+" "+dist
End Function


Function Map_ParseObject(stream,mesh=0)

	o.obj	= Obj_New()
	If mesh Then Obj_SetObj( o,mesh,name$)
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		Select Lower(util_trim2(lin$))
		Case "aem{"
			If environment = 1
				mesh = AEM_Parse(stream,mesh,env_sky)
			Else
				mesh = AEM_Parse(stream,mesh,map_ent)
			EndIf
			Obj_SetObj( o,mesh,name$)
		Case "afx{"
			If environment = 1
				mesh = FX_ParseAFX(stream,env_sky)
			Else
				mesh = FX_ParseAFX(stream)
			EndIf
			;mesh = FX_ParseAFX(stream)
			Obj_SetObj( o,mesh,name)
		Case "entity{"
			AEM_ParseEntity( stream,mesh )
		Case "cubic{"
			If o\entity Then Cub_ParseCubic(Stream,o.obj) ;Else DebugLog "PARSING ERROR: 'cubic{' > entity does'nt exist"
		Case "dot3{"
			If o\entity Then Dot3_ParseDot3(Stream,o.obj)
		Case "flag{"
			mesh = Fla_ParseFlag(stream,mesh)
		Case "gate{"
			mesh = Gate_ParseGate(stream,mesh)
		Default
			If Util_GetParas(lin$)
				Select Lower(paras[0])
				Case "name"
					name$ = paras[1]
				Case "environment"
					environment% = paras[1]
				End Select
			EndIf
		End Select
	Until Right(lin,1)="}"
	
	Select name 
	Case "EnvirMesh"
		map_ent = mesh
	Case "EnvirSun"
		;Menv_sun = mesh
	;Default
	;	If Left(name,5) = "Envir" Then
	;		EntityParent mesh, env_sky
	;	EndIf
	End Select
	
	If environment=1 Then EntityParent mesh, env_sky,0

	
End Function


Function Map_ParseConquest(stream)
	c.conquest = New conquest
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "flag"
					c\f		= Fla_FindFlagByID(paras[1])
				Case "ifholdby"
					c\ifholdby	= paras[1]
				Case "malus"
					c\malus	= paras[1]
				Case "for"
					c\Fort	= paras[1]
			End Select
		EndIf
	Until lin="}"
End Function


Function Map_Clear()

	FreeEntity map_ent
	If shi_specular <> 0 Then FreeTexture shi_specular
	If shi_specular2 <> 0 Then FreeTexture shi_specular2
	If shi_specular2additive <> 0 Then FreeTexture shi_specular2additive
	shi_specular = 0
	shi_specular2 = 0
	shi_specular2additive = 0
End Function