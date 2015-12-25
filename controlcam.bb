Global cc_camtarget
Global cc_piv
Global cc_cam,cam_cam
Global cc_camzoom# = 1
Global cc_camzoom2#= cc_camzoom
Global cc_mxs#
Global cc_camlight
Global cc_spectating
Global cc_range1#, cc_range2#

Global cc_pivx#
Global cc_pivy#
Global cc_pivz#
Global cc_camx#
Global cc_camy#
Global cc_camz#

Global inp_byrectsprite
Global cc_rectdrawing
Global cc_rectsx
Global cc_rectsy

Global cc_overviewinput=0
Global cc_overviewzoom# = 1.0

Global cc_mode
Global cc_target
Global cc_tzoom
Global cc_closeup# = 1

Global cc_grid = 0, cc_gridmode = 1
Global cc_grida# = 0
Global cc_firstperson = 0

Global cc_quake# = 0
Global cc_wmode = 0

Const cc_maxheight = 10
Global cc_outofspace

Const scroll_range = 20
Const scroll_speed# = 0.04
Global scrolling

Global cc_afterburners = LoadSound("SFX/ENGINE/rocketlp.wav")

Global cc_gridtex, cc_gridplane, cc_gridplane2
Global cc_superpiv
Global cc_allowbigmap

Global cc_profile = 0

Global cc_joystick = 0
Global cc_accelerate = 17
Global cc_decelerate = 31
Global cc_slideleft = 30
Global cc_slideright = 32
Global cc_rollleft = 16
Global cc_rollright = 18
Global cc_afterburner = 15

Global cc_fireprimary = 256
Global cc_firesecondary = 257
Global cc_specialweapon = 56
Global cc_input = 28

Global cc_targetspeed = 48
Global cc_retarget = 258
Global cc_retargetfighter = 21
Global cc_retargetbomber = 22
Global cc_retargetbigship = 23
Global cc_retargetfriend = 34
Global cc_retargetall = 35

Global cc_WireFrame = 66
Global cc_strmmap = 50
Global cc_showgrid = 51
Global cc_showsupport = 42

Global cc_camerathirdperson = 67
Global cc_camerafirstperson = 68
Global cc_camerachangetarget = 46
Global cc_zoomin = 49

Global cc_stopcommand = 45

Global cc_moveplaneup = 201
Global cc_moveplanedown= 209 

Global cc_pause = 25
Global cc_screenshot = 88
Global cc_setprofile1 = 62
Global cc_setprofile2 = 63


Function CC_LoadControls(file$)
	
	Util_CheckFile(file,0)
	stream = ReadFile(file)
	Repeat
		lin$ = ReadLine(stream)
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "maindevice"
				cc_joystick = (paras[1] = "joystick")
			Case "accelerate"
				cc_accelerate 		= paras[1]
			Case "decelerate"
				cc_decelerate 		= paras[1]
			Case "slideleft"
				cc_slideleft 		= paras[1]
			Case "slideright"
				cc_slideright 		= paras[1]
			Case "rollleft"
				cc_rollleft 		= paras[1]
			Case "rollright"
				cc_rollright 		= paras[1]
			Case "afterburner"
				cc_afterburner 		= paras[1]
			Case "fireprimary"
				cc_fireprimary 		= paras[1]
			Case "firesecondary"
				cc_firesecondary 	= paras[1]
			Case "specialweapon"
				cc_specialweapon	= paras[1]
			Case "targetspeed"
				cc_targetspeed 		= paras[1]
			Case "retarget"
				cc_retarget 		= paras[1]
			Case "retargetfighter"
				cc_retargetfighter 	= paras[1]
			Case "retargetbomber"
				cc_retargetbomber 	= paras[1]
			Case "retargetbigship"
				cc_retargetbigship	= paras[1]
			Case "retargetfriend"
				cc_retargetfriend 	= paras[1]
			Case "retargetall"
				cc_retargetall 		= paras[1]
			Case "wireframe"
				cc_WireFrame 		= paras[1]
			Case "showgrid"
				cc_showgrid 		= paras[1]
			Case "showsupport"
				cc_showsupport 		= paras[1]
			Case "camerathirdperson"
				cc_camerathirdperson = paras[1]
			Case "camerafirstperson"
				cc_camerafirstperson = paras[1]
			Case "camerachangetarget"
				cc_camerachangetarget = paras[1]
			Case "zoomin"
				cc_zoomin 			= paras[1]
			Case "screenshot"
				cc_screenshot 		= paras[1]
			Case "strategicalminimap"
				cc_strmmap			= paras[1]
			Case "stopcommand"
				cc_stopcommand		= paras[1]
			Case "pause"
				cc_pause			= paras[1]
			Case "profile1"
				cc_setprofile1		= paras[1]
			Case "profile2"
				cc_setprofile2		= paras[1]
			Case "specialweapon"
				cc_specialweapon	= paras[1]
			End Select
		EndIf
	Until Eof(stream)
	CloseFile stream

End Function

Function CC_SaveControls(id$="")
	If id <> "" Then id="_"+id
	stream = WriteFile(datad+"controls"+id+".ini")
	
	If cc_joystick=0 Then 
		WriteLine stream,"maindevice = mouse"
	Else
		WriteLine stream,"maindevice = joystick"
	EndIf
	WriteLine stream,"accelerate = "+cc_accelerate
	WriteLine stream,"decelerate = "+cc_decelerate
	WriteLine stream,"slideleft = "+cc_slideleft
	WriteLine stream,"slideright = "+cc_slideright
	WriteLine stream,"rollleft = "+cc_rollleft
	WriteLine stream,"rollright = "+cc_rollright
	WriteLine stream,"afterburner = "+cc_afterburner
	WriteLine stream,"fireprimary = "+cc_fireprimary
	WriteLine stream,"firesecondary = "+cc_firesecondary
	WriteLine stream,"specialweapon = "+cc_specialweapon
	WriteLine stream,"targetspeed = "+cc_targetspeed
	WriteLine stream,"retarget = "+cc_retarget
	WriteLine stream,"retargetfighter = "+cc_retargetfighter
	WriteLine stream,"retargetbomber = "+cc_retargetbomber
	WriteLine stream,"retargetbigship = "+cc_retargetbigship
	WriteLine stream,"retargetfriend = "+cc_retargetfriend
	WriteLine stream,"retargetall = "+cc_retargetall
	WriteLine stream,"wireframe = "+cc_WireFrame
	WriteLine stream,"showgrid = "+cc_showgrid
	WriteLine stream,"showsupport = "+cc_showsupport
	WriteLine stream,"camerathirdperson = "+cc_camerathirdperson
	WriteLine stream,"camerafirstperson = "+cc_camerafirstperson
	WriteLine stream,"camerachangetarget = "+cc_camerachangetarget
	WriteLine stream,"zoomin = "+cc_zoomin
	WriteLine stream,"screenshot = "+cc_screenshot
	WriteLine stream,"strategicalminimap = "+cc_strmmap
	WriteLine stream,"stopcommand = "+cc_stopcommand
	WriteLine stream,"profile1 = "+cc_setprofile1
	WriteLine stream,"profile2 = "+cc_setprofile2
	
	CloseFile stream

End Function

Function CC_Init()
	cc_camtarget= CreatePivot()
	
	cc_piv		= CreatePivot()
	cc_cam		= CreateCamera(cc_piv)
	
	cc_superpiv	= CreatePivot()
	
	;cc_camlight	= CreateLight()
	;LightColor cc_camlight,255,250,240
	cam_cam		= cc_cam
	wat_gamecam = cam_cam
	If main_bloom>0 Then InitGlow cc_cam, 0,0,main_width,main_height
	
	;CameraViewport cc_cam,0,GraphicsHeight()/8,GraphicsWidth(),GraphicsHeight()*6/8
	
	MoveEntity cc_camtarget,0,0,-5
	CameraZoom cc_cam,1
	
	MoveEntity cc_cam,0,0,-10
	PointEntity cc_cam,cc_piv
	CameraRange cc_cam,1,701
	CameraFogMode cc_cam,1
	CameraFogColor cc_cam,0,0,0
	;CameraClsColor cc_cam,200,220,240
	CameraFogRange cc_cam,400,700
	;InitGlow cc_cam, 0,0,main_width,main_height
	;CameraClsMode cc_cam,0,1
	
	cc_gridplane = CreatePivot()
	cc_gridplane2 = CreatePlane(1,cc_gridplane)
	cc_gridtex = LoadTexture(gfxd+"ENVIRON/map.png",1+2+8)
	EntityTexture cc_gridplane2,cc_gridtex
	ScaleTexture cc_gridtex,1000,1000
	HideEntity cc_gridplane
	EntityFX cc_gridplane2,1+16
	;EntityBlend cc_gridplane,3
	
	;AmbientLight 0,0,0
	WireFrame cc_wmode
	
	cc_quake = 0
End Function



Function CC_SetTarget(entity,mode=1,zoom#=1) ; mode   1: folgen  2: firstperson 5: gameover  6: minimap
	cc_camx = 0
	cc_camy = 0
	cc_camz = -10
	RotateEntity cc_cam,0,0,0
	
	cc_overviewinput = 0
	cc_overviewzoom = 1
	CameraFogMode cc_cam,1
	CameraRange cc_cam,cc_range1,cc_range2
	cc_mode		= mode
	cc_target	= entity
	If mode = 4
		EntityParent cc_piv,0
		Return
	EndIf
	If mode = 5
		cc_target = CreatePivot(cc_target)
		EntityParent cc_target,0
	EndIf
	
	cc_tzoom	= zoom#
	
	If mode < 5
		EntityParent cc_piv,cc_target
		PositionEntity cc_piv,0,0,0
		RotateEntity cc_piv,0,0,0
	Else
		EntityParent cc_piv,0
	EndIf
	If mode = 6 Then
		RotateEntity cc_piv,0,0,0
		CameraFogMode cc_cam,0
		CameraRange cc_cam,1,hud_mspace*1.8
		EntityParent cc_piv,cc_superpiv
	EndIf
	If mode = 2 Then EntityPickMode main_pl\mesh,0,0
	If mode = 3 Then
		PositionEntity cc_piv,Rnd(-20,20),Rnd(20),Rnd(-20,20)
		EntityParent cc_piv,0
		PointEntity cc_piv,cc_target
	EndIf
	dust_camerajump = 1
	cc_quake = 0
End Function

Function CC_Clear()
	FreeEntity cc_cam
	FreeEntity cc_piv
	FreeEntity cc_gridplane
	FreeTexture cc_gridtex
	FreeEntity cc_superpiv
	cc_target = 0
	cc_mode = 0
	cc_abst = 0
	bloom_pause = 0
	cc_gridmode = 1
	cc_grid = 0
	cc_cam = 0
End Function

Function CC_Update()
	
	CC_Control()
	
	ShowEntity cc_gridplane
	EntityAlpha cc_gridplane2, cc_grida
	cc_grida = (cc_grida * 4 + cc_grid) / 5.0
	If EntityY(cc_cam,1) < EntityY(cc_gridplane) Then RotateEntity cc_gridplane2,180,0,0 Else RotateEntity cc_gridplane2,0,0,0
	
	mz = MouseZSpeed()
	cc_camzoom	 = cc_camzoom - mz
	If cc_camzoom > cc_maxheight Then cc_camzoom = cc_maxheight ElseIf cc_camzoom < 0 Then cc_camzoom = 0
	
	cc_pivx = EntityX(cc_piv)
	cc_pivz = EntityZ(cc_piv)
	cc_pivy# = EntityY(cc_piv)
	
	cc_pivpit# = EntityPitch(cc_piv)
	cc_pivyaw# = EntityYaw(cc_piv)
	cc_pivrol# = EntityRoll(cc_piv)
	
	If cc_quake>.1 Then 
		cc_camx = 0 + Rnd(-.5,.5) * cc_quake
		cc_camy = 0 + Rnd(-.5,.5) * cc_quake
		cc_camz = -10 + Rnd(-.5,.5) * cc_quake
		RotateEntity cc_cam, Rnd(-1,1) * cc_quake, Rnd(-1,1) * cc_quake, Rnd(-1,1) * cc_quake
		cc_quake = cc_quake*.8 - .1
	Else
		cc_camx = 0
		cc_camy = 0
		cc_camz = -10
		RotateEntity cc_cam,0,0,0
	EndIf
	
	If cc_target
		Select cc_mode
		Case 1
			EntityParent cc_piv,cc_target
			cc_pivy# = cc_pivy - (cc_pivy-2.5-cc_tzoom)*.1
			cc_pivz# = cc_pivz - (cc_pivz+2*cc_tzoom)*.1
			EntityAlpha main_pl\mesh,1
		Case 2
			EntityParent cc_piv,cc_target
			cc_pivy# = cc_pivy - (cc_pivy)*.1
			cc_pivz# = cc_pivz - (cc_pivz-7)*.1
			EntityAlpha main_pl\mesh,0
		Case 3
			EntityParent cc_piv,0
			AlignToVector cc_piv,EntityX(cc_target)-cc_pivx,EntityY(cc_target)-cc_pivy,EntityZ(cc_target)-cc_pivz,2,.9
		End Select
	EndIf
	
	If Inp_KeyHit(cc_camerathirdperson)
		CC_SetTarget(main_pl\piv,1,main_pl\shc\CZoom)
		cc_firstperson = 0
	ElseIf Inp_KeyHit(cc_camerafirstperson)
		CC_SetTarget(main_pl\piv,2,main_pl\shc\CZoom)
		cc_firstperson = 1
	EndIf
	
	If Inp_KeyDown(cc_zoomin) And (hud_input\active = 0)
		cc_closeup = 3+(cc_closeup-3)*.9^main_gspe 
	Else
		cc_closeup = 1+(cc_closeup-1)*.9^main_gspe 
	EndIf
	
	cc_allowbigmap = 0
	bloom_pause = 0
	If cc_gridmode = 2 Then
		cc_allowbigmap = 1
		bloom_pause = 1
		Hud_UpdateBigMap()
	EndIf
	ScaleEntity cc_cam,1,1,(1-bloom_effect2*.1)*cc_closeup
	
	If cc_gridmode Then cc_grid = 1 Else cc_grid = 0
	
	PositionEntity cc_piv,cc_pivx,cc_pivy,cc_pivz
	PositionEntity cc_cam, cc_camx, cc_camy, cc_camz
End Function

Function CC_CamSpecChange()
	bloom_mb = 0 
	For s.ship = Each ship
		If s\team = main_pl\team And main_pl <> s And s\spawntimer <= 0 And s\shc\size < 8
			i = i + 1
		EndIf
	Next
	
	If i = 0
		CC_SetTarget(map_levstart,1,1)
	Else
		num2 = Rand(1,i)
		
		For s.ship = Each ship
			If main_pl <> s And s\spawntimer <= 0 And s\shc\size < 8
				num = num + 1
				If num = num2
					CC_SetTarget(s\piv,3,s\shc\size/2+1)
				EndIf
			EndIf
		Next
	EndIf
End Function


Function CC_CamUpdate()
	cc_allowbigmap = 0
	bloom_pause = 0
	
	If cc_mode <> 6
		HideEntity cc_gridplane
	Else
		cc_grida = 1
		ShowEntity cc_gridplane
		EntityAlpha cc_gridplane2, cc_grida
		cc_grida = (cc_grida * 4 + cc_grid) / 5.0
		If EntityY(cc_cam,1) < EntityY(cc_gridplane) Then RotateEntity cc_gridplane2,180,0,0 Else RotateEntity cc_gridplane2,0,0,0
	EndIf
	
	If cc_spectating = 2 Then cc_mode = 6
	
	If hud_input\active = 1
		If Inp_KeyHit(cc_input) Then
			CC_Message(hud_input\txt)
			hud_input\active = 0
			FlushKeys()
		EndIf
	Else
		If Inp_KeyHit(cc_input) Then
			HUD_ActivateInput(hud_input)
		EndIf
		
		If cc_spectating=1 And (Rand(2000) = 4 Or Inp_KeyHit(cc_camerachangetarget))
			CC_CamSpecChange()
		EndIf
		
		If Inp_KeyHit(cc_strmmap) And hud_mode = 5 Then HUD_SetMode(0) 
		
		
		If Inp_KeyHit(cc_moveplaneup)
			MoveEntity cc_gridplane,0,20,0
		EndIf
		If Inp_KeyHit(cc_moveplanedown)
			MoveEntity cc_gridplane,0,-20,0
		EndIf
		If Inp_KeyDown(cc_moveplaneup) And Inp_KeyDown(cc_moveplanedown)
			PositionEntity cc_gridplane,0,0,0
		EndIf

	EndIf
	
	If cc_camzoom > cc_maxheight Then cc_camzoom = cc_maxheight ElseIf cc_camzoom < 0 Then cc_camzoom = 0
	
	cc_pivx = EntityX(cc_piv)
	cc_pivz = EntityZ(cc_piv)
	cc_pivy# = EntityY(cc_piv)
	
	cc_pivpit# = EntityPitch(cc_piv)
	cc_pivyaw# = EntityYaw(cc_piv)
	cc_pivrol# = EntityRoll(cc_piv)
	
	cc_camx = EntityX( cc_cam )
	cc_camz = EntityZ( cc_cam )
	
	If cc_target Or cc_mode = 4 Or cc_mode = 6
		Select cc_mode
		Case 1
			EntityParent cc_piv,cc_target
			cc_pivy# = cc_pivy - (cc_pivy-3*cc_tzoom)*.1
			cc_pivz# = cc_pivz - (cc_pivz+2*cc_tzoom)*.1
			EntityAlpha main_pl\mesh,1
		Case 2
			EntityParent cc_piv,cc_target
			cc_pivy# = cc_pivy - (cc_pivy)*.1
			cc_pivz# = cc_pivz - (cc_pivz-7)*.1
			EntityAlpha main_pl\mesh,0
		Case 3
			dist# = EntityDistance(cc_piv,cc_target)
			EntityParent cc_piv,0
			If EntityPitch(cc_piv)<89 And EntityPitch(cc_piv)>-89 Then RotateEntity cc_piv,EntityPitch(cc_piv),EntityYaw(cc_piv),0
			TurnEntity cc_piv,Sin(MilliSecs()/100)*3,Sin(MilliSecs()/15+20)*4,0
			TurnEntity cc_piv,DeltaPitch(cc_piv,cc_target)/4,DeltaYaw(cc_piv,cc_target)/4,0
			TurnEntity cc_piv,-Sin(MilliSecs()/10)*3,-Sin(MilliSecs()/15+20)*4,0
			If EntityPitch(cc_piv)<89 And EntityPitch(cc_piv)>-89 Then RotateEntity cc_piv,EntityPitch(cc_piv),EntityYaw(cc_piv),0
			
			MoveEntity cc_piv,0,0,dist*.05
			
			If dist > 200
				PositionEntity cc_piv,0,0,0
				RotateEntity cc_piv,0,0,0
				EntityParent cc_piv,cc_target
				PositionEntity cc_piv,Rnd(-20,20),Rnd(20),Rnd(-20,20)
				EntityParent cc_piv,0
				PointEntity cc_piv,cc_target
			EndIf
		Case 4 ; zoomout
			MoveEntity cc_piv,0,0,-.5*main_gspe
		Case 5 ; pointto
			EntityParent cc_piv,0
			dx# = -cc_pivx+EntityX(cc_target)
			dy# = -cc_pivy+EntityY(cc_target)
			dz# = -cc_pivz+EntityZ(cc_target)
			AlignToVector cc_piv,dx,dy,dz,3,.15^(main_gspe#)
			dist# = EntityDistance(cc_piv,cc_target)
			Util_Approach(cc_piv,EntityX(cc_target)-dx*200.0/dist,EntityY(cc_target)-dy*200.0/dist,EntityZ(cc_target)-dz*200.0/dist,.25)
		Case 6 ; überblick
			Util_Approach(cc_piv,0,0,-hud_mspace*cc_overviewzoom,.15)
			If cc_overviewinput=0 Then Util_Approach(cc_superpiv, 0,0,0 ,.15)
			PointEntity cc_piv,cc_superpiv
			TurnEntity cc_piv,(1-util_minmax(EntityZ(cc_piv,0),-hud_mspace,0)/-Float(hud_mspace))*-35.0,0,0
			If MouseDown(3)
				RotateEntity cc_superpiv,util_minmax(EntityPitch(cc_superpiv)+MouseYSpeed(),0,80),EntityYaw(cc_superpiv)+MouseXSpeed(),0
				cc_overviewinput = 1
			Else
				If cc_overviewinput = 0
					AlignToVector cc_superpiv,-.5,-1,.5,3,.15^(1.0/main_gspe#)
					RotateEntity cc_superpiv,EntityPitch(cc_superpiv),EntityYaw(cc_superpiv),0
				EndIf
				bla=MouseYSpeed() + MouseXSpeed()
			EndIf
			cc_overviewzoom = cc_overviewzoom - MouseZSpeed()*0.05
			cc_overviewzoom = util_minmax(cc_overviewzoom,0.1,1.5)
			If hud_input\active = 0
				If Inp_KeyDown(cc_accelerate) Or my < 5
					TranslateEntity cc_superpiv, -Sin(EntityYaw(cc_superpiv)) * main_gspe*15, 0, Cos(EntityYaw(cc_superpiv)) * main_gspe*15
					cc_overviewinput = 1
				EndIf
				If Inp_KeyDown(cc_decelerate) Or my > main_height-5
					TranslateEntity cc_superpiv, Sin(EntityYaw(cc_superpiv)) * main_gspe*15, 0, -Cos(EntityYaw(cc_superpiv)) * main_gspe*15
					cc_overviewinput = 1
				EndIf
				
				If Inp_KeyDown(cc_slideleft) Or mx < 5
					TranslateEntity cc_superpiv, -Cos(EntityYaw(cc_superpiv)) * main_gspe*15, 0, -Sin(EntityYaw(cc_superpiv)) * main_gspe*15
					cc_overviewinput = 1
				EndIf
				If Inp_KeyDown(cc_slideright) Or mx > main_width-5
					TranslateEntity cc_superpiv, Cos(EntityYaw(cc_superpiv)) * main_gspe*15, 0, Sin(EntityYaw(cc_superpiv)) * main_gspe*15
					cc_overviewinput = 1
				EndIf
				PositionEntity cc_superpiv, util_minmax(EntityX(cc_superpiv),-map_radius,map_radius), util_minmax(EntityY(cc_superpiv),-map_radius,map_radius), util_minmax(EntityZ(cc_superpiv),-map_radius,map_radius)
			EndIf
		End Select
	EndIf
	
	ScaleEntity cc_cam,1,1,1-bloom_effect2*.2
End Function

Function CC_Control()
	If Inp_KeyHit(cc_setprofile1) Then
		cc_profile = 0
		CC_LoadControls(datad$+"controls_"+cc_profile+".ini")
	ElseIf Inp_KeyHit(cc_setprofile2) Then
		cc_profile = 1
		CC_LoadControls(datad$+"controls_"+cc_profile+".ini")
	EndIf
	
	spe# = main_gspe
	If spe>3/main_pl\shc\Turnspeed Then spe = 3
	
	x# = (MouseX()-main_hwidth)*-1.301/main_hwidth
	y# = (MouseY()-main_hheight)*1.301/main_hheight
	
	If cc_joystick Then
		x# = JoyX()*-1.301
		y# = JoyY()*-1.301
		MoveMouse main_width/2, main_height/2
	EndIf
	
	If Inp_KeyDown(cc_camerachangetarget)
		RotateEntity cc_piv,util_minmax(EntityPitch(cc_piv)+y,-80,80),EntityYaw(cc_piv)+x,0
	Else
		RotateEntity cc_piv,0,0,0
		main_pl\tsyaw = main_pl\tsyaw-(main_pl\tsyaw-x#)*.3*spe*main_pl\shc\Turnspeed
		main_pl\tspitch = main_pl\tspitch-(main_pl\tspitch-y)*.3*spe*main_pl\shc\Turnspeed
	EndIf
	
	
	If hud_input\active = 1
		If Inp_KeyHit(cc_input) Then
			CC_Message(hud_input\txt)
			hud_input\active = 0
			FlushKeys()
		EndIf
	Else
		If Inp_KeyHit(cc_strmmap)
			HUD_SetMode(5)
		EndIf
		
		If Inp_KeyHit(cc_showgrid)
			cc_gridmode = (cc_gridmode + 1) Mod 3
		EndIf
		If cc_grid Then
			If Inp_KeyHit(cc_moveplaneup)
				MoveEntity cc_gridplane,0,20,0
			EndIf
			If Inp_KeyHit(cc_moveplanedown)
				MoveEntity cc_gridplane,0,-20,0
			EndIf
			If Inp_KeyDown(cc_moveplaneup) And Inp_KeyDown(cc_moveplanedown)
				PositionEntity cc_gridplane,0,0,0
			EndIf
		EndIf
		
		
		If Inp_KeyDown(cc_accelerate)
			main_pl\zzs = main_pl\zzs + main_pl\shc\Speedup * main_gspe
		EndIf
		If Inp_KeyDown(cc_decelerate)
			main_pl\zzs = main_pl\zzs - main_pl\shc\speeddown * main_gspe
		EndIf
		
		If Inp_KeyDown(cc_slideleft)
			main_pl\xs = main_pl\xs - main_pl\shc\slidespeedup * main_gspe
		EndIf
		If Inp_KeyDown(cc_slideright)
			main_pl\xs = main_pl\xs + main_pl\shc\slidespeedup * main_gspe
		EndIf
		
		If Inp_KeyDown(cc_rollleft)
			main_pl\roll = main_pl\roll + main_pl\shc\rollspeedup * main_gspe
		EndIf
		If Inp_KeyDown(cc_rollright)
			main_pl\roll = main_pl\roll - main_pl\shc\rollspeedup * main_gspe
		EndIf
		
		If Inp_KeyDown(cc_afterburner)
			If main_pl\burnafter = 0 And main_pl\afterburner > main_pl\shc\afterburnertime / 2
				main_pl\burnafter = 1
				PlaySound(cc_afterburners)
			EndIf
		Else
			main_pl\burnafter = 0
			bloom_mb = 0 
		EndIf
		
		If Inp_KeyHit(cc_targetspeed)
			If main_pl\target <> Null Then
				main_pl\zzs = main_pl\target\zzs
			EndIf
		EndIf
		
		If Inp_KeyDown(cc_fireprimary)
			Shi_Fire(Handle(main_pl),1,Handle(main_pl\target))
		EndIf
		
		If Inp_KeyDown(cc_firesecondary)
			Shi_Fire(Handle(main_pl),2,Handle(main_pl\target))
		EndIf
		
		If Inp_KeyDown(cc_specialweapon)
			Shi_Fire(Handle(main_pl),4,Handle(main_pl\target))
		EndIf
		
		Shi_Fire(Handle(main_pl),3,Handle(main_pl\target))
		
		If Inp_KeyHit(cc_retarget) Then
			HUD_Retarget()
		ElseIf Inp_KeyHit(cc_retargetfighter)
			HUD_Retarget(1)
		ElseIf Inp_KeyHit(cc_retargetbomber)
			HUD_Retarget(2)
		ElseIf Inp_KeyHit(cc_retargetbigship)
			HUD_Retarget(3)
		ElseIf Inp_KeyHit(cc_retargetfriend)
			HUD_Retarget(0,2)
		ElseIf Inp_KeyHit(cc_retargetall)
			HUD_Retarget(0,1)
		EndIf
		
		If Inp_KeyHit(cc_WireFrame) Then WireFrame cc_wmode : cc_wmode = 1 - cc_wmode
		
		If Inp_KeyHit(cc_input) Then
			HUD_ActivateInput(hud_input)
		EndIf
	EndIf
End Function


Function CC_Message(txt$,noname=0)
	comtxt$ =""
	If txt <> ""
		If Not CC_ParseCommand(txt$)
			If Left(txt,9) = "Command: "
				comtxt$ = Com_ParseCommand(Mid(txt,10))
			ElseIf noname=0 Then
				txt = "<"+net_name+"> "+txt
			EndIf
			
			If net = 0
				If comtxt<>"" Then 
					If comtxt$ <> "-" Then HUD_PrintLog(comtxt$,0)
				Else
					HUD_PrintLog(txt$)
				EndIf
			ElseIf net_isserver = 1
				If comtxt<>"" Then 
					If comtxt$ <> "-" Then HUD_PrintLog(comtxt$,0)
				Else
					HUD_PrintLog(txt$)
				EndIf
				AddUDPByte(C_Message)
				AddUDPByte(0) ; an alle
				AddUDPString(txt$)
			ElseIf Left(txt,9) <> "Command: " Or main_pl = teamid[main_pl\team]\commander
				AddUDPByte(S_Message)
				AddUDPByte(0) ; an alle
				AddUDPString(txt$)
			EndIf
		EndIf
	EndIf
End Function

Function CC_ParseCommand(txt$)
	If Left(txt,1) = "/"
		Util_GetParas(txt," ")
		Select paras[0]
		Case "/name", "/nick"
			If net = 0
				Shi_ChangeName(main_pl,paras[1])
			ElseIf net_isserver
				Shi_ChangeName(main_pl,paras[1])
				AddUDPByte(C_NameChange)
				AddUDPByte(main_pl\id)
				AddUDPString(paras[1])
			Else
				AddUDPByte(S_NameChange)
				AddUDPString(paras[1])
			EndIf
		Case "/kick"
			If net_isserver
				For u.UDP_User = Each UDP_User
					If u\name = paras[1]
						UDP_KickUser(u\id)
					EndIf
				Next
			Else
				HUD_PrintLog("Only the host can kick users.",255,255,0)
			EndIf
		Case "/removebot"
			If net=0 Or net_isserver=1
				ts.ship = Null
				For s.ship = Each ship
					If s\name = paras[1] And s\human = 0
						sp.spawn = Fla_FindSpawnByID(s\selspawn)
						If sp <> Null Then 
							If sp\s <> s Then 
								ts = s
								Exit
							EndIf
						Else
							ts = s
							Exit
						EndIf
					EndIf
				Next
				If ts <> Null Then 
					HUD_PrintLog("Removed bot "+paras[1]+".")
					Shi_DeleteShip(Handle(ts))
				Else
					HUD_PrintLog("There's no bot called "+paras[1]+"!",255,255,0)
				EndIf
			EndIf
		Case "/addbot"
			If net=0 Or net_isserver = 1
				If paras[1] = 1
					Shi_CreateShip(0,0,0 ,1,KI_BotName(1),1,1)
				Else
					Shi_CreateShip(0,0,0 ,1,KI_BotName(2),2,1)
				EndIf
			EndIf
		Case "/dedicate"
			Main_Dedicate = 1-Main_Dedicate
		Case "/pause"
			Game_SetPause(1-Game_Pause)
		Case "/map"
			If FileType("MAPS/"+paras[1]) = 1 And (net=0 Or net_isserver=1)
				Main_MapChange = paras[1]
				Main_QuitGame = 1
			Else
				HUD_PrintLog("Mapfile does not exist.",255,255,0)
			EndIf
		Case "/team"
			If net_isserver
				HUD_PrintLog("Team: <"+net_name+"> "+Trim(Right(txt$,Len(txt)-5)))
				AddUDPByte(C_Message)
				AddUDPByte(main_pl\team) ; an team
				AddUDPString("<"+net_name+"> "+Trim(Right(txt$,Len(txt)-5)))
			ElseIf net
				AddUDPByte(S_Message)
				AddUDPByte(main_pl\team) ; an team
				AddUDPString("<"+net_name+"> "+Trim(Right(txt$,Len(txt)-5)))
			Else
				HUD_PrintLog("Team: <"+net_name+"> "+Trim(Right(txt$,Len(txt)-5)))
			EndIf
		Case "/teambalance"
			If net=0 Or net_isserver = 1
				Game_TeamBalance = paras[1]
			EndIf
		Case "/nexttrack"
			Music_NextTrack()
		Case "/music"
			Music_SetVolume(paras[1])
		Case "/quit"
			Main_QuitGame = 1
		Case "/crash"
			Print 20 / Int(Sin(0))
			RuntimeError paras[1]
		Case "/help"
			HUD_PrintLog("/name [new name]")
			HUD_WriteLog(" Changes player name.",255,255,0)
			HUD_PrintLog("/team [message]")
			HUD_WriteLog(" Sends a message to team mates only.",255,255,0)
			
			HUD_PrintLog(" --- ",255,255,0)
			HUD_PrintLog("/pause")
			HUD_WriteLog(" Pauses the game. (Server)",255,255,0)
			HUD_PrintLog("/map [mapFileName.amap]")
			HUD_WriteLog(" Ends the round and changes the map. (Server)",255,255,0)
			HUD_PrintLog("/dedicate")
			HUD_WriteLog(" Disables graphical output.",255,255,0)
			
			HUD_PrintLog(" --- ",255,255,0)
			HUD_PrintLog("/addbot [teamId (1 / 2)]")
			HUD_WriteLog(" Adds a new bot player. (Server)",255,255,0)
			HUD_PrintLog("/removebot [botname]")
			HUD_WriteLog(" Removes a bot player. (Server)",255,255,0)
			HUD_PrintLog("/teambalance [ratio]")
			HUD_WriteLog(" Sets team balance (1 = 1:1, 2 = 2:1, 0.5 = 1:2) (Only affects bots!)",255,255,0)
			
			HUD_PrintLog(" --- ",255,255,0)
			HUD_PrintLog("/kick [netPlayerName]")
			HUD_WriteLog(" Kicks a player that is connected via the network. (Server)",255,255,0)
			
			HUD_PrintLog(" --- ",255,255,0)
			HUD_PrintLog("/music [volume 0..1]")
			HUD_WriteLog(" Sets music volume (0 = silent, 1 = loud)",255,255,0)
			HUD_PrintLog("/nexttrack")
			HUD_WriteLog(" Skips the current music track.",255,255,0)
			
			HUD_PrintLog(" --- ",255,255,0)
			HUD_PrintLog("/help")
			HUD_WriteLog(" Displays this help",255,255,0)
			HUD_PrintLog("/quit")
			HUD_WriteLog(" Quits the game. (Server)",255,255,0)
		Default
			HUD_PrintLog("Unknown command. Enter /help to display a list of commands.",255,255,0)
		End Select
		Return 1
	EndIf
	Return 0
End Function

Function CC_GetMessage()
	team = ReadUDPByte()
	txt$ = ReadUDPString()
	If Left(txt,9) = "Command: "
		txt$ = Com_ParseCommand(Mid(txt,10))
	EndIf
	If team=0 And txt <> "-" Then HUD_PrintLog(txt$)
	If main_pl<> Null Then 
		If team=main_pl\team And txt <> "-" Then HUD_PrintLog("Team: " + txt$)
	EndIf
End Function

Function CC_GetCMessage()
	team = ReadUDPByte()
	txt$ = ReadUDPString()
	
	ctxt$ = txt	
	If Left(txt,9) = "Command: "
		ctxt$ = Com_ParseCommand(Mid(txt,10))
	EndIf
	
	AddUDPByte(C_Message)
	AddUDPByte(team)
	AddUDPString(txt$)
	
	If team=0 And txt <> "-" Then HUD_PrintLog(txt$)
	If team=main_pl\team And txt <> "-"  Then HUD_PrintLog("Team: " + txt$)
End Function

Function CC_GetNameChange()
	id = ReadUDPByte()
	txt$ = ReadUDPString()
	s.ship = ships(id)
	If s <> Null
		Shi_ChangeName(s,txt)
	EndIf
End Function

Function CC_GetCNameChange(s.ship)
	txt$ = ReadUDPString()
	If s <> Null
		Shi_ChangeName(s,txt)
		AddUDPByte(C_NameChange)
		AddUDPByte(s\id)
		AddUDPString(txt)
	EndIf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D