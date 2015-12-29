Type hud_mobj
	Field ent
	Field sprite,msline
	Field sprite2,sline
	Field aura,size#
End Type

Global hud_minimap
Global hud_minimaptex
Global hud_minimapcam

Global hud_minicam

Global hud_mv[3]

Global hud_mspace#

Global Hud_MZoom# = 4.0
Global hud_minimode = 0


Function Hud_InitMinimap()
	hud_minimapcam = CreateCamera()
	CameraClsMode hud_minimapcam,0,1
	HideEntity hud_minimapcam
	CameraZoom hud_minimapcam,6
	PositionEntity hud_minimapcam,0,64000,64000
	
	hud_minimap = CreateMesh(hud_minimapcam)
	sur			= CreateSurface(hud_minimap)
	
	hud_minimaptex = LoadTexture(hudd+"navimap.png",1+2+8+512)
	EntityTexture hud_minimap,hud_minimaptex
	
	hud_mv[0]	= AddVertex(sur,	-1,1,0,		0,0)
	hud_mv[1]	= AddVertex(sur,	1,1,0,		1,0)
	hud_mv[2]	= AddVertex(sur,	-1,-1,0,	0,1)
	hud_mv[3]	= AddVertex(sur,	1,-1,0,		1,1)
	
	AddTriangle sur, hud_mv[0], hud_mv[2], hud_mv[1]
	AddTriangle sur, hud_mv[1], hud_mv[2], hud_mv[3]
	
	UpdateNormals(hud_minimap)
	
	ScaleEntity hud_minimap,64,64,64
	
	EntityFX hud_minimap,1+8+16
	EntityBlend hud_minimap,1
	
	TurnEntity hud_minimap,60,0,0
	PositionEntity hud_minimap,0,-15,550
	EntityParent hud_minimap,0
	
	Hud_MiniCam = LoadSprite("GFX/GUI/camera.png",1+2,hud_minimap)
	;SpriteViewMode hud_minicam,2
	;EntityFX hud_minicam,1+8
	ScaleSprite hud_minicam,2,2
	EntityOrder hud_minicam,-12
	
	EntityOrder hud_minimap,-11
End Function

Function HUD_ClearMiniMap()
	FreeEntity hud_minimap
	FreeEntity hud_minimapcam
	FreeTexture hud_minimaptex
	Delete Each hud_mobj
	
	hud_minimaptex = 0
End Function

Function Hud_SetMinimap(path$)
	hud_minimaptex = LoadTexture(path$,1+2+8+512)
	EntityTexture hud_minimap,hud_minimaptex
End Function

Function Hud_UpdateMinimap()
	xs# = hud_mzoom/hud_mspace
	zs# = hud_mzoom/hud_mspace
	
	x#	= EntityX(cc_cam,1)*xs
	z#	= EntityZ(cc_cam,1)*zs
	y#  = EntityY(cc_cam,1)*xs
	
	;PositionEntity hud_minicam,EntityX(cc_cam,1)*xs-x,EntityZ(cc_cam,1)*zs-z,EntityY(cc_cam,1)*xs-y
	;RotateSprite hud_minicam,EntityYaw(cc_cam,1)
	
	RotateEntity hud_minimap,50,0,0
	
	For o.hud_mobj = Each hud_mobj
		If hud_mode <> 5 And hud_mode <> 1 And (cc_allowbigmap=0 Or main_pl\mmap=o\sprite2) Then HideEntity o\sprite2 HideEntity o\sline
		;fx# = EntityX(o\ent,1)*xs-x
		;fy# = EntityZ(o\ent,1)*zs-z
		;fz# = -EntityY(o\ent,1)*xs+y
		
		TFormPoint 0,0,0, o\ent,cc_cam
		fx# = TFormedX()*xs
		fy# = TFormedZ()*zs
		fz# = -TFormedY()*xs
		
		If fx*fx+fy*fy+fz*fz > 1 Then
			HideEntity o\sprite
		Else
			ShowEntity o\sprite
			PositionEntity o\sprite,fx,fy,fz
			PositionEntity o\msline,0,0,-fz/2
			ScaleEntity o\msline,.01,.01,Abs(fz/2.0)
			;RotateSprite o\sprite,EntityYaw(o\ent,1)
		EndIf
	Next
	
	;PositionTexture hud_minimaptex,.5+1/hud_mzoom/2-x/hud_mzoom/2,.5+1/hud_mzoom/2+z/hud_Mzoom/2
	
	;ScaleTexture hud_minimaptex,hud_mzoom,hud_mzoom
	
	Select Hud_Minimode
	Case 0
		;Util_Approach(hud_minimap,60,35,80,.1)
		;PointEntity hud_minimap,cc_cam
		;TurnEntity hud_minimap,0,180,0
		;RotateEntity hud_minimap,EntityPitch(hud_minimap),EntityYaw(hud_minimap)+180,0
	Case 1
		;Util_Approach(hud_minimap,4,-2,18,.1)
		;PointEntity hud_minimap,cc_cam
		;TurnEntity hud_minimap,0,180,0
		;RotateEntity hud_minimap,EntityPitch(hud_minimap),EntityYaw(hud_minimap)+180,0
	End Select
	
	ShowEntity hud_minimap
	
	If hud_reallyquit + hud_pause = 0 And hud_mode = 0 And main_showminimap = 1 Then
		HideEntity cc_cam
		ShowEntity hud_minimapcam
		
		CameraViewport hud_minimapcam,780*main_width/1024,30*main_height/hud_height,250*main_width/1024,250*main_width/1024
		
		RenderWorld()
		
		HideEntity hud_minimapcam
		ShowEntity cc_cam
	EndIf
End Function

Function Hud_UpdateBigMap()
	For o.hud_mobj = Each hud_mobj
		fx# = EntityX(o\ent,1)
		fz# = EntityZ(o\ent,1)
		fy# = EntityY(o\ent,1)-EntityY(cc_gridplane)
		PositionEntity o\sprite2,fx,fy,fz
		
		If o\aura <> 1 Then
			t# = o\size/30+o\size*EntityDistance(o\sprite2,cc_cam)/5000
		Else
			t# = o\size
		EndIf
		
		If o\aura = 2 And cc_gridmode = 2 Then t = 0
		
		ScaleSprite o\sprite2,t,t
			
		;If fy > 0 Then EntityOrder o\sprite2,-5.5 Else EntityOrder o\sprite2,-4.5
		If Abs(fy) > 30 And ( cc_allowbigmap<>1 Or main_pl\mmap <> o\sprite2 ) And o\aura = 0 Then
			ShowEntity o\sline
			PositionEntity o\sline,fx,fy/2,fz
			ScaleEntity o\sline,5,Abs(fy)/2,5
		Else
			HideEntity o\sline
		EndIf
		If cc_allowbigmap<>1 Or main_pl\mmap <> o\sprite2 Then ShowEntity o\sprite2
	Next
End Function


Function Hud_AddMinimapObject(ent,sprite,parent=0,size#=1,aura=0,size2#=0)
	o.Hud_mobj	= New hud_mobj
	o\ent		= ent
	o\sprite	= CopyEntity(sprite,hud_minimap)
	o\sprite2	= CopyEntity(sprite,cc_gridplane)
	o\size 		= size
	o\aura		= aura
	If parent<>0 Then EntityParent o\sprite,parent
	EntityOrder o\sprite,-12-(parent<>0)
	If aura=0 Then ScaleSprite o\sprite,size2,size2 Else ScaleSprite o\sprite,5,5
	;EntityOrder o\sprite2,-5
	ScaleSprite o\sprite2,size,size
	HideEntity o\sprite2
	
	o\sline = CopyEntity(hud_cube, cc_gridplane)
	EntityFX o\sline,1+8
	;EntityOrder o\sline,-5
	HideEntity o\sline
	If aura Then EntityAlpha o\sline,0
	
	o\msline = CopyEntity(o\sline,o\sprite)
	EntityOrder o\msline,-13
	
	xs# = hud_mzoom/hud_mspace
	zs# = hud_mzoom/hud_mspace
	
	x#	= EntityX(cc_cam,1)*xs
	z#	= EntityZ(cc_cam,1)*zs
	
	fx# = EntityX(o\ent,1)*xs-x
	fy# = EntityZ(o\ent,1)*zs-z
	
	If fx<-1 Or fx>1 Or fy<-1 Or fy>1 Then
		HideEntity o\sprite
	Else
		ShowEntity o\sprite
		PositionEntity o\sprite,fx,fy,0
		;RotateSprite o\sprite,EntityYaw(o\ent,1)
	EndIf
	
	fx# = EntityX(o\ent,1)
	fz# = EntityZ(o\ent,1)
	fy# = EntityY(o\ent,1)
	PositionEntity o\sprite2,fx,fy,fz
	If o\aura = 0 Then
		t# = o\size/30+o\size*EntityDistance(o\sprite2,cc_cam)/5000
	Else
		t# = o\size
	EndIf
	
	ScaleSprite o\sprite2,t,t
	
	Return o\sprite2
End Function

Function Hud_MColor(spr,r,g,b,a#=1)
	For o.Hud_mobj = Each Hud_mobj
		If o\sprite2 = spr Then
			EntityColor o\sprite,r,g,b
			EntityColor o\sprite2,r,g,b
			EntityColor o\sline,r,g,b
			EntityColor o\msline,r,g,b
			EntityAlpha o\sprite,a
			EntityAlpha o\sprite2,a
			EntityAlpha o\sline,a
			EntityAlpha o\msline,a
		EndIf
	Next
End Function

Function Hud_MSize(spr,size#)
	For o.Hud_mobj = Each Hud_mobj
		If o\sprite2 = spr Then
			o\size = size
		EndIf
	Next
End Function

Function Hud_RemoveFromMinimap(ent,sprite)
	For o.hud_mobj = Each hud_mobj
		If o\ent = ent Or o\sprite2 = sprite Then 
			FreeEntity o\sprite
			FreeEntity o\sprite2
			FreeEntity o\sline
			Delete o
		EndIf
	Next
End Function

Function EntityYawB#(ent)
	Return VectorYaw(-GetMatElement(ent,0,2),0,GetMatElement(ent,2,2))
End Function