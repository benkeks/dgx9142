Const envd$ = gfxd$+"ENVIRON/"
;Global env_plane
;Global env_ter,env_tertex
Global env_sky,env_skytex
Global env_sun,env_sunbrightness#,env_sunbright#
Global env_plan
Global env_sea,env_seachan
Global env_wind,env_windchan
Global env_music,env_musicchan
Global env_normal, env_normaldif
Global env_skymode = 0

Function Env_Init()
	env_sky = CreatePivot()
	
	env_normal = LoadTexture("GFX/ENVIRON/normalpixels.png",1+128)
	SetCubeMode env_normal,1
	
	env_normaldif = LoadTexture("GFX/ENVIRON/normalpixels.png",1)
	SetCubeMode env_normaldif,2
	
	env_sea = LoadSound("SFX/lake.wav")
	env_wind = LoadSound("SFX/wind.mp3")
	LoopSound env_wind
End Function

Function Env_Update()
	
	;PositionEntity env_sun,EntityX(cc_cam,1),EntityY(cc_cam,1),EntityZ(cc_cam,1)
	If env_skymode = 1
		PositionEntity env_sky,EntityX(cc_cam,1),EntityY(cc_cam,1),EntityZ(cc_cam,1)
		RotateEntity env_sky,EntityPitch(cc_cam,1),EntityYaw(cc_cam,1),EntityRoll(cc_cam,1)
		MoveEntity env_sky,0,0,100 / ((1-bloom_effect2*.1)*cc_closeup)
	Else
		PositionEntity env_sky,EntityX(cc_cam,1),EntityY(cc_cam,1),EntityZ(cc_cam,1)
	EndIf
	
	y = EntityY(cc_cam,1)
	For su.wat_surf = Each wat_surf
		If Abs(EntityY(su\piv,1)-y) < 500
			If ChannelPlaying(env_seachan) = 0 Then env_seachan = PlaySound(env_sea)
			ChannelVolume env_seachan,(500-Abs(EntityY(su\piv,1)-y))/500.00
		EndIf
	Next
	
	If ChannelPlaying(env_windchan) = 0 And map_atmo=1 Then
		env_windchan = PlaySound(env_wind)
	EndIf
End Function

Function Env_Clear()
	StopChannel env_windchan
	StopChannel env_seachan
	FreeSound env_wind
	FreeSound env_sea
	FreeEntity env_sun2
	FreeEntity env_sky
	env_sky = 0
	FreeTexture env_skytex
	FreeTexture env_normal
End Function

Function Env_LoadSkyBox( parent=0 )
	m=CreateMesh(parent)
	
	;front face
	b=LoadBrush( envd+"starground2.bmp")
	;BrushAlpha b,.1
	s=CreateSurface( m,b )
	v1=AddVertex(s,-1,+1,-1,0,0):v2=AddVertex (s,+1,+1,-1,1,0)
	v3=AddVertex(s,+1,-1,-1,1,1):v4=AddVertex (s,-1,-1,-1,0,1)
	AddTriangle s,v1,v2,v3:AddTriangle s,v1,v3,v4
	;right face
	v1=AddVertex (s,+1,+1,-1,0,0):v2=AddVertex (s,+1,+1,+1,1,0)
	v3=AddVertex (s,+1,-1,+1,1,1):v4=AddVertex (s,+1,-1,-1,0,1)
	AddTriangle s,v1,v2,v3:AddTriangle s,v1,v3,v4
	;back face
	v1=AddVertex (s,+1,+1,+1,0,0):v2=AddVertex (s,-1,+1,+1,1,0)
	v3=AddVertex (s,-1,-1,+1,1,1):v4=AddVertex (s,+1,-1,+1,0,1)
	AddTriangle s,v1,v2,v3:AddTriangle s,v1,v3,v4
	;left face
	v1=AddVertex (s,-1,+1,+1,0,0):v2=AddVertex (s,-1,+1,-1,1,0)
	v3=AddVertex (s,-1,-1,-1,1,1):v4=AddVertex (s,-1,-1,+1,0,1)
	AddTriangle s,v1,v2,v3:AddTriangle s,v1,v3,v4
	;top face
	v1=AddVertex (s,-1,+1,+1,0,1):v2=AddVertex (s,+1,+1,+1,0,0)
	v3=AddVertex (s,+1,+1,-1,1,0):v4=AddVertex (s,-1,+1,-1,1,1)
	AddTriangle s,v1,v2,v3:AddTriangle s,v1,v3,v4
	;bottom face	
	v1=AddVertex (s,-1,-1,-1,1,0):v2=AddVertex (s,+1,-1,-1,1,1)
	v3=AddVertex (s,+1,-1,+1,0,1):v4=AddVertex (s,-1,-1,+1,0,0)
	AddTriangle s,v1,v2,v3:AddTriangle s,v1,v3,v4
	FreeBrush b
	
	ScaleMesh m,100,100,100
	EntityOrder m,10
	FlipMesh m
	EntityFX m,1+8
	EntityColor m,300,300,300
	Return m
End Function