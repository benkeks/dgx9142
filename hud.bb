Global hudd$ = gfxd$+"GUI/"

Global hud_cr,hud_cg,hud_cb

Global hud_mode
Global hud_piv
Global hud_mainu
Global hud_selected, hud_selected2
Global hud_aim,hud_aim2

Global hud_sstatepic
Global hud_sunit1
Global hud_crhair_ab, hud_crhair_weaps, hud_crhair_hps, hud_crhair_shield
Global hud_speed,hud_afterburner.gadget
Global hud_health.gadget,hud_shields.gadget
Global hud_psstatepic=0
Global hud_estatepic,hud_esstatepic
Global hud_support
Global hud_extracam

Global hud_tshow
Global hud_t.ship,hud_tpiv
Global hud_thealth.gadget, hud_tshields.gadget
Global hud_tname, hud_tname2, hud_tdist, hud_tmini

Global hud_nextt.ship
Global hud_nexttpointer, hud_nextta

Global hud_ctshow, hud_ctarget, hud_comorder, hud_comtext

Global hud_font
Global hud_panel
Global hud_but[6]
Global hud_countd
Global hud_buttontex
Global hud_arrow
Global hud_paneltab.gadget, hud_paneltabtex
Global hud_nospawn.gadget, hud_nospawntex
Global hud_stopspawn.gadget

Global hud_showcselect
Global hud_showcsanim#

Global hud_score
Global hud_tickets1,hud_tickets2
Global hud_sfract1,hud_sfract2
Global hud_flags[12]

Global hud_spawnpiv
Global hud_spawn
Global hud_selspawn
Global hud_selectswitch

Global hud_start
Global hud_sttxt1
Global hud_sttxt2

Global hud_end

Global hud_weappiv
Global hud_weapbg[4]
Global hud_weaptxt[4]
Global hud_weapreload.gadget[4]
Global hud_weapammo[4]
Global hud_weapp, hud_weappower.gadget
Global hud_input.hudinput

Global hud_ishippiv, hud_iscount

Type iship
	Field s.ship
	Field name
	Field hp
End Type

Global hud_warning, hud_warningt
Global hud_button, hud_radio

Global hud_height

Global hud_reallyquit, hud_rqm
Global hud_pause, hud_pausemesh

Global hud_leavingground
Global hud_lastglwarning#

Global hud_continue.gadget

Global hud_targetlock

Global hud_cube

Function HUD_Init()
	hud_height = 512 * main_hheight / main_hwidth
	
	tex = LoadTexture(hudd$+"progress.png",1+2)
	
	hud_cube = CreateCube()
	HideEntity hud_cube
	
	GUI_Init(cc_cam)
	HUD_InitLog()
	
	hud_iscount = 0
	
	hud_font = Txt_LoadFont(hudd$+"font.png",1+2,5,13,hudd+"font.inf")
	
	hud_piv = CreatePivot(cc_cam)
	PositionEntity hud_piv,-512,hud_height,512
	
	hud_mainu = Util_LoadSprite(hudd$+"crosshair.png",1+2,hud_piv)
	EntityAlpha hud_mainu,.8
	EntityFX hud_mainu,1+8
	EntityOrder hud_mainu,-10
	PositionEntity hud_mainu,512,-hud_height,0
	ScaleMesh hud_mainu,64,64,1
	
	hud_crhair_ab = Util_LoadSprite(hudd$+"crosshairinfo.png",1+2,hud_mainu)
	EntityFX hud_crhair_ab,1+8
	EntityOrder hud_crhair_ab,-11
	EntityColor hud_crhair_ab,245,150,5
	ScaleMesh hud_crhair_ab,64,64,1
	
	hud_crhair_weaps = CopyEntity(hud_crhair_ab,hud_mainu)
	RotateEntity hud_crhair_weaps,0,0,-90
	EntityColor hud_crhair_weaps,5,205,5
	
	hud_crhair_hps = CopyEntity(hud_crhair_ab,hud_mainu)
	RotateEntity hud_crhair_hps,0,0,90
	EntityColor hud_crhair_hps,250,0,0
	
	hud_crhair_shield = CopyEntity(hud_crhair_ab,hud_mainu)
	RotateEntity hud_crhair_shield,0,0,180
	EntityColor hud_crhair_shield,15,105,250
	
	
	hud_tshow = Util_LoadSprite(hudd$+"pointer.png",1+2,hud_piv)
	PositionMesh hud_tshow,0,1,0
	EntityFX hud_tshow,1+8
	EntityOrder hud_tshow,-8
	PositionEntity hud_tshow,512,-hud_height,0
	ScaleEntity hud_tshow,16,128,1
	HideEntity hud_tshow
	
	hud_ctshow = CopyEntity(hud_tshow,hud_piv)
	ScaleEntity hud_ctshow,14,100,1
	
	hud_ctarget = LoadSprite(hudd+"comtarget.png",1+2)
	EntityOrder hud_ctarget,-4
	EntityFX hud_ctarget,1+8
	ScaleSprite hud_ctarget,20,20
	HideEntity hud_ctarget
	
	hud_nexttpointer = Util_LoadSprite(hudd+"pointer2.png",1+2,hud_piv)
	PositionMesh hud_nexttpointer,0,1,0
	EntityFX hud_nexttpointer,1+8
	EntityOrder hud_nexttpointer,-9
	PositionEntity hud_nexttpointer,512,-hud_height,0
	ScaleMesh hud_nexttpointer,16,128,1
	EntityColor hud_nexttpointer,255,0,0
	HideEntity hud_nexttpointer
	
	hud_nextta = Util_LoadSprite(hudd$+"aim.png",1+2,cc_cam)
	EntityFX hud_nextta,1+8+16
	EntityOrder hud_nextta,-7
	ScaleMesh hud_nextta,12,12,1
	EntityColor hud_nextta,255,0,0
	HideEntity hud_nextta
	
	; weapons ########################
	
	hud_weappiv = CreatePivot(hud_piv)
	PositionEntity hud_weappiv,922+10,-hud_height*2+220-10,0
	
	For i = 0 To 4
		hud_weapbg[i] = Util_LoadSprite(hudd$+"weaps.png",1+2,hud_weappiv)
		PositionEntity hud_weapbg[i],0,-i*46,0 
		EntityFX hud_weapbg[i],1+8+16
		EntityOrder hud_weapbg[i],-8
		ScaleMesh hud_weapbg[i],64,32,1
		hud_weaptxt[i] = CreatePivot()
		hud_weapammo[i] = CreatePivot()
		
		; weapon reload time bar
		hud_weapreload[i]  = GUI_AddGadget(3,-52,-12,104,8,hud_weapbg[i],2)
		EntityColor hud_weapreload[i]\mesh,5,205,5
		EntityTexture hud_weapreload[i]\mesh,tex 
	Next
	
	; target ########################
	hud_selected = Util_LoadSprite(hudd$+"target_lowerpart.png",1+2,hud_piv)
	PositionMesh hud_selected,0,-1,0
	EntityFX hud_selected,1+8+16
	EntityOrder hud_selected,-6
	ScaleMesh hud_selected,64,32,1
	ScaleEntity hud_selected,1.2,1.2,1.2
	HideEntity hud_selected
	
	hud_selected2 = Util_LoadSprite(hudd$+"target_upperpart.png",1+2,hud_selected)
	PositionMesh hud_selected2,0,1,0
	EntityFX hud_selected2,1+8+16
	EntityOrder hud_selected2,-6
	ScaleMesh hud_selected2,64,32,1
	HideEntity hud_selected2
	
	hud_tmini = Util_CreateSprite(hud_selected2)
	EntityFX hud_tmini,1+8+16
	EntityOrder hud_tmini,-12
	ScaleMesh hud_tmini,100,100,1
	PositionEntity hud_tmini, 35, 55,0
	
	hud_aim = Util_LoadSprite(hudd$+"aim.png",1+2,cc_cam)
	EntityFX hud_aim,1+8+16
	EntityOrder hud_aim,-7
	ScaleMesh hud_aim,8,8,1
	HideEntity hud_aim
	
	hud_aim2 = Util_LoadSprite(hudd$+"aim.png",1+2,cc_cam)
	EntityFX hud_aim2,1+8+16
	EntityOrder hud_aim2,-7
	ScaleMesh hud_aim2,16,16,1
	HideEntity hud_aim2
	
	
	; target info
	; hull indicator
	hud_thealth = GUI_AddGadget(3,-52,-24,104,8,hud_selected,0)
	EntityColor hud_thealth\mesh,250,0,0
	EntityTexture hud_thealth\mesh,tex
	
	; shield indicator
	hud_tshields = GUI_AddGadget(3,-52,-34,104,8,hud_selected,0)
	EntityColor hud_tshields\mesh,15,105,250
	EntityTexture hud_tshields\mesh,tex
	
	hud_tname = CreatePivot(hud_selected2)
	hud_tname2 = CreatePivot(hud_selected2)
	hud_tdist = CreatePivot(hud_selected)
	
	; target lock indicator for missiles and the like
	hud_targetlock = LoadSprite(hudd$+"targetlock.png",1+2)
	ScaleSprite hud_targetlock,40,40
	EntityOrder hud_targetlock,-8
	EntityFX hud_targetlock,1+8+16
	
	; player ship info #############################################
	
	hud_sunit1 = Util_LoadSprite(hudd$+"shipinfo.png",1+2,hud_piv)
	ScaleMesh hud_sunit1,128,128,1
	EntityOrder hud_sunit1,-10
	PositionEntity hud_sunit1,918+10,-hud_height*2+100-10,50
	EntityFX hud_sunit1,1+8+16
	
	hud_support = Util_LoadSprite(hudd$+"support.png",1+2,hud_sunit1)
	PositionEntity hud_support,50,58,0
	ScaleMesh hud_support,8,8,1
	EntityOrder hud_support,-11
	EntityFX hud_support,1+8+16
	
	hud_speed = CreatePivot(hud_sunit1)
	hud_afterburner = GUI_AddGadget(3,89,-84,12,-152,hud_sunit1,2) ; nachbrenner
	EntityColor hud_afterburner\mesh,245,150,5
	EntityTexture hud_afterburner\mesh,tex
	
	hud_weappower  = GUI_AddGadget(3,76,-84,12,-152,hud_sunit1,2) ; waffenenergie
	EntityColor hud_weappower\mesh,5,205,5
	EntityTexture hud_weappower\mesh,tex 
	
	hud_health = GUI_AddGadget(3,-92,-59,152,12,hud_sunit1,2) ; hülle
	EntityColor hud_health\mesh,250,0,0
	EntityTexture hud_health\mesh,tex
	hud_shields = GUI_AddGadget(3,-92,-72,152,12,hud_sunit1,2) ; schilde
	EntityColor hud_shields\mesh,15,105,250
	EntityTexture hud_shields\mesh,tex
	
	hud_sstatepic = Util_LoadSprite(hudd$+"shield.png",1+2,hud_sunit1)
	ScaleMesh hud_sstatepic,40,40,1
	EntityOrder hud_sstatepic,-11
	
	hud_psstatepic = CopyEntity(hud_sstatepic,hud_sunit1)
	PositionEntity hud_psstatepic,-27,8,0
	HideEntity hud_psstatepic
	
	hud_extracam = CreateCamera()
	CameraClsMode hud_extracam,0,1
	CameraZoom hud_extracam,6
	CameraViewport hud_extracam,(900+10)*main_width/1024,(hud_height*2-120-10)*main_height/1024,255,255
	PositionEntity hud_extracam,0,64000,0
	TurnEntity hud_extracam,90,0,0
	HideEntity hud_extracam
	
	hud_comorder = Util_LoadSprite(hudd+"command.png",1+2, hud_piv)
	ScaleMesh hud_comorder,128,64,1
	EntityOrder hud_comorder,-12
	PositionEntity hud_comorder,512,-200,0
	hud_comtext = CreatePivot()
	
	hud_tpiv = CreatePivot()
	
	Hud_InitMinimap()
	
	hud_spawnpiv= CreatePivot(hud_minimap)
	hud_spawn	= LoadSprite(hudd$+"spawn.png",1+2)
	HideEntity hud_spawn
	
	Hud_InitCommander()
	
	hud_ishippiv = CreatePivot(cc_cam)
	PositionEntity hud_ishippiv,-480,100,512
	
	;score / ticket count ###############
	hud_score = Util_LoadSprite(hudd$+"score.png",1+2,cc_cam)
	EntityFX hud_score,1+8+16
	PositionEntity hud_score,410+10,hud_height-50,512
	ScaleMesh hud_score,64,32,1
	EntityOrder hud_score,-11
	
	hud_tickets1 = CreatePivot(hud_score)
	hud_tickets2 = CreatePivot(hud_score)
	
	hud_sfract1 = Util_CreateSprite(hud_score)
	ScaleMesh hud_sfract1,8,8,1
	EntityFX hud_sfract1,1+8+16
	PositionEntity hud_sfract1,-44,12,0
	EntityOrder hud_sfract1,-13
	
	hud_sfract2 = Util_CreateSprite(hud_score)
	ScaleMesh hud_sfract2,8,8,1
	EntityFX hud_sfract2,1+8+16
	PositionEntity hud_sfract2,12,12,0
	EntityOrder hud_sfract2,-13
	
	For i = 1 To 12
		hud_flags[i] = Util_CreateSprite(hud_score)
		ScaleMesh hud_flags[i],3,3,1
		PositionEntity hud_flags[i],-50+i*8,-7,0
		EntityOrder hud_flags[i],-13
		HideEntity hud_flags[i]
	Next
	
	
	; class selection gui #################################
	
	hud_countd = CreatePivot(gui_par)
	
	hud_panel = LoadTexture(hudd$+"panel.png",1+2)
	g.gadget = GUI_AddGadget(1,-90,56,50,100)
	EntityTexture g\mesh,hud_panel
	
	hud_paneltabtex = LoadTexture(hudd$+"paneltab.png",1+2)
	hud_paneltab = GUI_AddGadget(1,-90,56,50,50,0,1)
	EntityParent hud_paneltab\mesh, g\mesh
	GUI_GadTexture(Handle(hud_paneltab),hud_paneltabtex)
	
	hud_nospawntex = LoadTexture(hudd$+main_lang+"nospawn.png",1+2)
	hud_nospawn = GUI_AddGadget(1,-90,-5,50,25,0,3)
	EntityParent hud_nospawn\mesh, g\mesh
	GUI_GadTexture(Handle(hud_nospawn),hud_nospawntex)
	
	hud_stopspawn = GUI_AddGadget(2,-85,-13,40,10,0,2)
	GUI_SetCaption(Handle(hud_stopspawn), lang_stop_spawn)
	GUI_SetHotKey(Handle(hud_stopspawn), 11)
	EntityColor hud_stopspawn\mesh,255,200,150
	
	g.gadget = GUI_AddGadget(2,-85,52,19,19,0,2)
	hud_but[0] = Handle(g)
	
	g.gadget = GUI_AddGadget(2,-64,52,19,19,0,2)
	hud_but[1] = Handle(g)
	
	g.gadget = GUI_AddGadget(2,-85,30,40,10,0,2)
	GUI_SetHotKey(Handle(g), 2)
	hud_but[2] = Handle(g)
	
	g.gadget = GUI_AddGadget(2,-85,20,40,10,0,2)
	GUI_SetHotKey(Handle(g), 3)
	hud_but[3] = Handle(g)
	
	g.gadget = GUI_AddGadget(2,-85,10,40,10,0,2)
	GUI_SetHotKey(Handle(g), 4)
	hud_but[4] = Handle(g)
	
	g.gadget = GUI_AddGadget(2,-85,0,40,10,0,2)
	GUI_SetHotKey(Handle(g), 5)
	hud_but[5] = Handle(g)
	
	hud_buttontex = LoadTexture(hudd+"button.png",1+2)
	GUI_GadTexture(hud_but[2],hud_buttontex)
	GUI_GadTexture(hud_but[3],hud_buttontex)
	GUI_GadTexture(hud_but[4],hud_buttontex)
	GUI_GadTexture(hud_but[5],hud_buttontex)
	GUI_GadTexture(Handle(hud_stopspawn),hud_buttontex)
	
	g.gadget = GUI_AddGadget(2,-46,-28,7,7, 0,2)
	EntityAlpha g\mesh,.9
	hud_but[6] = Handle(g)
	hud_arrow = LoadTexture(hudd+"arrow.png",1+2)
	GUI_GadTexture(hud_but[6], hud_arrow)
	GUI_SetMidHandle(hud_but[6])
	
	;chat input
	hud_input = HUD_CreateInput()
	EntityParent hud_input\piv, hud_logpiv
	PositionEntity hud_input\piv,0,-7,0
	
	;start tasks
	hud_start = CreatePivot(cc_cam)
	PositionEntity hud_start,-80,0,400
	
	;really quit?
	hud_reallyquit = 0
	hud_rqm = Util_CreateSprite(cc_cam)
	EntityBlend hud_rqm,2
	EntityColor hud_rqm,50,30,30
	ScaleMesh hud_rqm,300,300 * main_hheight / main_hwidth,1
	PositionEntity hud_rqm,0,0,300
	EntityOrder hud_rqm,-25
	
	t = Txt_Text(lang_quit_game, hud_font, hud_rqm)
	EntityOrder t ,-26
	PositionEntity t,0,0,0
	PositionMesh t,-MeshWidth(t)/2,-MeshHeight(t)/2,0
	
	HideEntity hud_rqm
	
	;pause
	hud_pause = 0
	hud_pausemesh = Util_CreateSprite(cc_cam)
	EntityBlend hud_pausemesh,2
	EntityColor hud_pausemesh,50,30,30
	ScaleMesh hud_pausemesh,300,300 * main_hheight / main_hwidth,1
	PositionEntity hud_pausemesh,0,0,300
	EntityOrder hud_pausemesh,-23
	
	t = Txt_Text(lang_pause, hud_font, hud_pausemesh)
	EntityOrder t ,-24
	PositionEntity t,0,0,0
	PositionMesh t,-MeshWidth(t)/2,-MeshHeight(t)/2,0
	
	HideEntity hud_pausemesh	
	
	;leaving the ground
	hud_leavingground = CreatePivot()
	
	;sounds
	hud_warning = LoadSound("SFX/GUI/WARNING2.wav")
	hud_button	= LoadSound("SFX/GUI/button.wav")
	hud_radio	= LoadSound("SFX/GUI/radio.ogg")
	
	hud_end = CreatePivot()
	
	hud_continue = GUI_AddGadget(2, -20,-50,40,10,0,2)
	EntityParent hud_continue\mesh, hud_start
	
	GUI_GadTexture(Handle(hud_continue),hud_buttontex)
	GUI_SetCaption(Handle(hud_continue), lang_continue)
	
	HUD_InitPlayers()
End Function

Function HUD_Clear()
	FreeTexture hud_buttontex
	FreeTexture hud_paneltabtex
	FreeTexture hud_nospawntex
	FreeSound hud_button
	FreeSound hud_warning
	FreeSound hud_radio
	FreeEntity hud_targetlock
	FreeEntity hud_ctarget
	FreeEntity hud_rqm
	FreeEntity hud_piv
	FreeEntity hud_extracam
	FreeEntity hud_cube
	Delete Each hud_mobj
	Delete Each iship
	hud_estatepic	= 0
	hud_esstatepic	= 0
	hud_psstatepic	= 0
	main_pl\target.ship = Null
End Function

Function HUD_SetColor(r,g,b)
	hud_cr = r
	hud_cg = g
	hud_cb = b
End Function

Function HUD_SetPlayer()
	PositionEntity hud_weappiv,922+10,-hud_height*2+250,0
	
	For i = 0 To 4
		If main_pl\weapgroup[i] = 0
			HideEntity hud_weapbg[i]
		Else
			MoveEntity hud_weappiv,0,46,0
			ShowEntity hud_weapbg[i]
		EndIf
	Next
	
	For i = 0 To 3
		GUI_SetGadActivity(hud_but[2+i])
	Next
	GUI_SetGadActivity(hud_but[1+main_pl\selclass],2)
End Function

Function HUD_SetMode(mode)
	FlushKeys
	FlushMouse
	mh = 0
	mlh = 0
	hud_mode = mode
	HideEntity hud_aim
	HideEntity hud_aim2
	HideEntity hud_targetlock
	hud_lastglwarning = 1000
	HideEntity hud_leavingground
	hud_commandermode = 0
	HideEntity hud_compiv
	HideEntity hud_nextta
	
	For s.ship = Each ship
		HideEntity s\hudhl
	Next
	
	Select mode
	Case 0 ; normal
		t = main_pl\selspawn
		GUI_SetVis()
		HUD_SetSpawns(0,100)
		main_pl\selspawn = t
		
		ShowEntity hud_piv
		ShowEntity hud_aim
		ShowEntity hud_aim2
		HideEntity gui_par
		HideEntity hud_start
		ShowEntity hud_ishippiv
		ShowEntity hud_targetlock
		
		cc_spectating = 0
		
		CC_SetTarget(main_pl\piv,1+cc_firstperson,main_pl\shc\CZoom)
		
		ShowEntity hud_selected
		
		Hud_MZoom# = 4
		hud_minimode = 0
	Case 1 ; auswahl
		HUD_SetSpawns(main_pl\selclass,main_pl\team)
		GUI_SetVis()
		
		HideEntity hud_piv
		ShowEntity gui_par
		HideEntity hud_start
		HideEntity hud_ishippiv
		
		main_pl\target.ship = Null
		HUD_ChangeTarget(0)
		hud_selectswitch = MilliSecs()+2000*(cc_mode=1 Or cc_mode=2)
		If cc_mode=6 Then
			CC_SetTarget(0,6)
			cc_spectating = 2
		Else
			CC_SetTarget(0,4)
		EndIf
		HideEntity hud_selected
		
		hud_showcselect = 1
		GUI_SetTurn(hud_but[6],180*(1-hud_showcselect))
		hud_showcsanim = -50
		Hud_MZoom# = 4
		hud_minimode = 0
		bloom_mb = 0
		
		If net<>0 And net_isserver=1 Then Shi_SendSpawnData(main_pl)
	Case 2 ; briefing
		GUI_SetVis()
		HUD_SetSpawns(0,100)
		
		hud_showcsanim = 0
		EntityParent hud_continue\mesh, hud_start
		GUI_SetGadgetActivity(hud_continue,1)
		
		HideEntity hud_piv
		HideEntity gui_par
		ShowEntity hud_start
		HideEntity hud_end
		HideEntity hud_ishippiv
				
		cc_spectating = 1
		
		main_pl\target.ship = Null
		HUD_ChangeTarget(0)
		CC_SetTarget(0,4)
		HideEntity hud_selected
		
		Hud_MZoom# = 4
		hud_minimode = 0
		bloom_mb = 0
		
		FlushKeys 
	Case 3 ; nix
		HUD_SetSpawns(0,100)
		
		HideEntity hud_piv
		HideEntity gui_par
		HideEntity hud_start
		HideEntity hud_minimap
		HideEntity hud_ishippiv
		
		cc_spectating = 1
		
		main_pl\target.ship = Null
		HUD_ChangeTarget(0)
		HideEntity hud_selected
		
		Hud_MZoom# = 4
		hud_minimode = 0
	Case 4 ; ende
		GUI_SetVis()
		HUD_SetSpawns(0,100)
		
		HideEntity hud_piv
		HideEntity gui_par
		HideEntity hud_start
		HideEntity hud_ishippiv
		ShowEntity hud_end
		
		cc_spectating = 0
		
		main_pl\target.ship = Null
		HUD_ChangeTarget(0)
		HideEntity hud_selected
		
		Hud_MZoom# = 4
		hud_minimode = 0
		
		HUD_EndDialog()
		
		hud_showcsanim = 0
		EntityParent hud_continue\mesh, hud_end
	Case 5 ; strategische karte
		HUD_SetSpawns(main_pl\selclass,main_pl\team)
		
		HideEntity hud_piv
		HideEntity gui_par
		HideEntity hud_start
		HideEntity hud_end
		ShowEntity hud_ishippiv
		
		cc_spectating = 2
		CC_SetTarget(0,6)
		HideEntity hud_selected
		
		Hud_MZoom# = 4
		hud_minimode = 0
		bloom_mb = 0
	End Select
End Function

Function HUD_Show()
	ShowEntity hud_score
	Select hud_mode
	Case 0
		ShowEntity hud_piv
		HideEntity gui_par
		HideEntity hud_start
	Case 1
		HideEntity hud_piv
		ShowEntity gui_par
		HideEntity hud_start
	Case 2
		HideEntity hud_piv
		HideEntity gui_par
		ShowEntity hud_start
	Case 3
		HideEntity hud_piv
		HideEntity gui_par
		HideEntity hud_start
		HideEntity hud_minimap
		HideEntity hud_selected
	Case 4
		HideEntity hud_piv
		HideEntity gui_par
		ShowEntity hud_end
	End Select
End Function

Function HUD_Hide()
	HideEntity hud_piv
	HideEntity gui_par
	HideEntity hud_start
	HideEntity hud_score
	HideEntity hud_ishippiv
End Function

Function Hud_AddIShip(s.ship)
	is.IShip= New iship
	is\s	= s
	is\name	= Txt_Text(s\name, hud_font, hud_ishippiv)
	MoveEntity is\name,0,-20*hud_iscount,0
	ScaleEntity is\name,2,2,2
	EntityOrder is\name ,-11
	EntityColor is\name,s\colr,s\colg,s\colb
	hud_iscount = hud_iscount + 1
End Function

Function HUD_SetSpawns(class,team)
	For o.hud_mobj = Each hud_mobj
		If GetParent(o\sprite) = hud_spawnpiv
			FreeEntity o\sprite
			FreeEntity o\sprite2
			FreeEntity o\sline
			Delete o
		EndIf
	Next
	
	s2.spawn = Fla_FindSpawnByID(main_pl\selspawn)
	
	For s.spawn = Each spawn
		If s\class = class And s\f\team = team And s\f\takeper >= 90 And s\typ = 1
			tsize = 0
			If main_pl\selspawn <> 0
				If s2\f = s\f
					tsize = 100
				EndIf
			EndIf
			s\maps = Hud_AddMinimapObject(s\piv,hud_spawn,hud_spawnpiv,100+tsize)
			ShowEntity s\maps
			Hud_MSize s\maps,100
			EntityOrder s\maps,-1
			If main_pl\selspawn <> 0
				If s2\f = s\f
					main_pl\selspawn = s\id
					Hud_MSize s\maps,200
				EndIf
			EndIf
		Else
			s\maps = 0
		EndIf
	Next
	
	s2.spawn = Fla_FindSpawnByID(main_pl\selspawn)
	
	If s2 <> Null
		If s2\f\team <> team Then main_pl\selspawn = 0
	EndIf
	
End Function

Function HUD_SetText(txt$,par,x=0,y=0)
	hud_txt = Txt_Text(txt$, hud_font, par)
	PositionEntity hud_txt,x,y,-20
	ScaleEntity hud_txt,1.5,1.5,1.5
	EntityOrder hud_txt,-14
	Return hud_txt
End Function

Function HUD_Update()
	If Inp_KeyDown(59) Then
		HUD_ShowPlayers()
	Else 
		HUD_HidePlayers()
	EndIf
	
	If hud_reallyquit
		ShowEntity hud_rqm
		If KeyDown(28) Then Main_QuitGame = 1
		If gkey <> "" And (Not KeyDown(1)) Then hud_reallyquit = 0
	Else
		HideEntity hud_rqm
	EndIf
	
	If hud_pause
		ShowEntity hud_pausemesh
	Else
		HideEntity hud_pausemesh
	EndIf
	
	Select main_pl\order
	Case ORDER_ATTACK
		PositionEntity hud_ctarget, EntityX(main_pl\oship\piv),EntityY(main_pl\oship\piv),EntityZ(main_pl\oship\piv),1
		PointEntity hud_ctarget,cc_cam
		MoveEntity hud_ctarget,0,0,EntityDistance(cc_cam,hud_ctarget)*.8
		EntityColor hud_ctshow,255,0,0
		ShowEntity hud_ctshow
		EntityColor hud_ctarget,255,0,0
		ShowEntity hud_ctarget
		
		ShowEntity hud_comorder
		If EntityName(hud_comtext)<>"Attack "+main_pl\oship\name Then
			FreeEntity hud_comtext
			
			hud_comtext = Txt_Text(lang_attack, hud_font, hud_comorder)
			EntityOrder hud_comtext,-15
			PositionEntity hud_comtext ,0,33,0
			PositionMesh hud_comtext ,-MeshWidth(hud_comtext)/2,-MeshHeight(hud_comtext )/2,0
			ScaleEntity hud_comtext,1.8,1.8,1.8
			EntityColor hud_comtext, 255,0,0
			NameEntity hud_comtext, "Attack "+main_pl\oship\name
			
			t = Txt_Text(main_pl\oship\name, hud_font, hud_comtext)
			EntityOrder t,-15
			PositionEntity t ,0,-11,0
			PositionMesh t ,-MeshWidth(t)/2,-MeshHeight(t )/2,0
		EndIf
	Case ORDER_MOVETO
		PositionEntity hud_ctarget, EntityX(main_pl\opiv),EntityY(main_pl\opiv),EntityZ(main_pl\opiv),1
		PointEntity hud_ctarget,cc_cam
		MoveEntity hud_ctarget,0,0,EntityDistance(cc_cam,hud_ctarget)*.8
		EntityColor hud_ctshow,255,255,255
		ShowEntity hud_ctshow
		EntityColor hud_ctarget,255,255,255
		ShowEntity hud_ctarget
		
		ShowEntity hud_comorder
		ox = EntityX(main_pl\opiv)
		oy = EntityY(main_pl\opiv)
		oz = EntityZ(main_pl\opiv)
		If EntityName(hud_comtext)<>"Move to "+ox+", "+oy+", "+oz Then
			FreeEntity hud_comtext
			
			hud_comtext = Txt_Text(lang_moveto, hud_font, hud_comorder)
			EntityOrder hud_comtext,-15
			PositionEntity hud_comtext ,0,33,0
			PositionMesh hud_comtext ,-MeshWidth(hud_comtext)/2,-MeshHeight(hud_comtext )/2,0
			ScaleEntity hud_comtext,1.8,1.8,1.8
			EntityColor hud_comtext, 255,255,0
			NameEntity hud_comtext, "Move to "+ox+", "+oy+", "+oz
			
			f.flag = Fla_FindFlagByPos.flag(ox,oy,oz, 150)
			If f <> Null Then
				t = Txt_Text(f\name, hud_font, hud_comtext)
			Else
				t = Txt_Text(ox+", "+oy+", "+oz, hud_font, hud_comtext)
			EndIf
			EntityOrder t,-15
			PositionEntity t ,0,-11,0
			PositionMesh t ,-MeshWidth(t)/2,-MeshHeight(t )/2,0
		EndIf
		
		If EntityDistance(main_pl\piv, main_pl\opiv) < 300 Then
			main_pl\order = 0
			main_pl\opiv = 0
		EndIf
	Default
		HideEntity hud_ctshow
		HideEntity hud_ctarget
		HideEntity hud_comorder
	End Select 
	
	If hud_mode <> 0 Then HideEntity hud_ctshow
	
	Select hud_mode
	Case 0	
		GUI_Update()
		EntityAlpha hud_psstatepic,main_pl\shields / main_pl\shc\Shields
		If main_pl\indanger < 10000
			hud_warningt = hud_warningt + 2*main_gspe
			If hud_warningt > main_pl\indanger
				PlaySound hud_warning
				hud_warningt = -1
			EndIf
		Else
			hud_warningt = 10000
		EndIf
		
		If main_pl\leavingtheground > 0 Then
			FreeEntity hud_leavingground
			hud_leavingground = Txt_Text(lang_leaving_the_ground, hud_font, cc_cam)
			EntityOrder hud_leavingground,-20
			PositionEntity hud_leavingground ,0,10,300
			PositionMesh hud_leavingground ,-MeshWidth(hud_leavingground)/2,-MeshHeight(hud_leavingground )/2,0
			EntityColor hud_leavingground, 255,0,0
			tf# = 1.0+Float(main_pl\leavingtheground Mod 1000)/2500.0
			If tf < hud_lastglwarning Then PlaySound hud_warning
			hud_lastglwarning = tf
			ScaleEntity hud_leavingground,tf,tf,1
			t = Txt_Text(Int((15000-main_pl\leavingtheground)/1000), hud_font, hud_leavingground)
			EntityOrder t ,-20
			PositionEntity t,0,-20,0
			EntityColor t, 255,0,0
			PositionMesh t,-MeshWidth(t)/2,-MeshHeight(t)/2,0
		Else 
			hud_lastglwarning = 1000
			HideEntity hud_leavingground
		EndIf
		
		If main_pl\supported
			ShowEntity hud_support
		Else
			HideEntity hud_support
		EndIf
		
		FreeEntity hud_speed
		hud_speed = Txt_Text(Int(main_pl\realspeed*200)+" mph", hud_font, hud_sunit1)
		ScaleEntity hud_speed,1.5,1.5,1
		EntityOrder hud_speed, -11
		PositionEntity hud_speed, -50-MeshWidth(hud_speed),-53,0
		
		hud_afterburner\value2 = main_pl\shc\afterburnertime
		hud_afterburner\value = main_pl\afterburner
		
		hud_health\value2 = main_pl\shc\Hitpoints
		hud_health\value = main_pl\hitpoints
		
		hud_shields\value2 = main_pl\shc\Shields
		hud_shields\value = main_pl\shields
		
		hud_weappower\value2 = main_pl\shc\power
		hud_weappower\value = main_pl\power
		
		EntityAlpha hud_crhair_ab,hud_afterburner\value / Float(hud_afterburner\value2)
		EntityAlpha hud_crhair_weaps, hud_weappower\value / Float(hud_weappower\value2)
		EntityAlpha hud_crhair_shield, hud_shields\value / Float(hud_shields\value2)
		EntityAlpha hud_crhair_hps, hud_health\value / Float(hud_health\value2)
		
		If hud_reallyquit + hud_pause = 0 And main_showminiplayer=1 Then HUD_RenderPlayer()
		
		hud_needssupport = 0
		
		If Inp_KeyDown(cc_showsupport) Then hud_needssupport = 1
			
		For i = 0 To 4
			id = main_pl\weapgroup[i]
			If id = 0 Then
				;Exit
			Else
				hud_weaptxt[i] = Txt_UpdateText(hud_weaptxt[i],weaponid[id]\name, hud_font, hud_weapbg[i])
				PositionEntity hud_weaptxt[i],-40,-8,0
				ScaleEntity hud_weaptxt[i],1.4,1.5,1.5
				EntityOrder hud_weaptxt[i],-11
				
				count = 0
				reload = weaponid[main_pl\weapgroup[i]]\reload
				ammo = 0
				ammomax = 0
				id2		= main_pl\shc\Weapsig
				For i2 = 1 To 10
					twg = weapsigi(id2,i2,1) 
					If weapsigi(id2,i2,1) = i
						count = count + 1
						ammo = main_pl\weapammo[twg]
						ammomax = main_pl\weapammomax[twg]
						If main_pl\weapreload[i2] < reload
							reload = main_pl\weapreload[i2]
						EndIf
					EndIf
				Next
				
				hud_weapreload[i]\value2 = weaponid[main_pl\weapgroup[i]]\reload
				hud_weapreload[i]\value = weaponid[main_pl\weapgroup[i]]\reload - reload
				
				EntityAlpha hud_weapbg[i],1
				EntityAlpha hud_weaptxt[i],1
				
				If weaponid[id]\neammo > 0
					hud_weapammo[i] = Txt_UpdateText(hud_weapammo[i], ammo, hud_font, hud_weapbg[i])
					PositionEntity hud_weapammo[i],33,-8,0
					ScaleEntity hud_weapammo[i],1.4,1.5,1.5
					EntityOrder hud_weapammo[i],-12
					If ammo <= 0 Then
						EntityAlpha hud_weapbg[i],0.3
						EntityAlpha hud_weaptxt[i],0.3
					EndIf
					If ammo < ammomax / 3 Then hud_needssupport = 1
				Else
					HideEntity hud_weapammo[i]
				EndIf
			EndIf
		Next
		
		For is.IShip = Each iship
			If is\hp <> 0 Then
				FreeEntity is\hp
				is\hp = 0
			EndIf
			If is\s\spawntimer <= 0 Then
                                txt$ = Chr(9)+"1[#progr{"+Int(is\s\hitpoints * 100.0 / Float(is\s\shc\hitpoints))+"}]" + Chr(13); Int(100*is\s\hitpoints/is\s\shc\hitpoints)
                                is\hp = Txt_UpdateText(is\hp, txt, hud_font, is\name)
				MoveEntity is\hp,45,0,0
				EntityOrder is\hp ,-11
				EntityColor is\hp,is\s\colr,is\s\colg,is\s\colb
				EntityAlpha is\name,1
			Else
				EntityAlpha is\name,.5
			EndIf
		Next
		
		If main_pl\order Then
			dy# = DeltaYaw(cc_cam,hud_ctarget)
			dp# = DeltaPitch(cc_cam,hud_ctarget)
			tdist# = Sqr(dy#^2+dp#^2)
			If tdist>=30 Then
				ShowEntity hud_ctshow
				twinkel# = ((ATan2(-dy,dp)+360) Mod 360) - EntityRoll(cc_cam,1)
				RotateEntity hud_ctshow,0,0,twinkel+180
			Else
				HideEntity hud_ctshow
			EndIf
		EndIf
		
		sels = 0
		selz = 2000
		supportdist = 4000
		support.ship = Null
		For s.ship = Each ship
			If s\team <> main_pl\team And s\spawntimer <= 0
				TFormPoint EntityX(s\piv,1),EntityY(s\piv,1),EntityZ(s\piv,1),0,cc_cam
				x#	= TFormedX()
				y#	= TFormedY()
				z#	= TFormedZ()
				If z > 0 And z < 2000 And s\stealthed = 0
					PositionEntity s\hudhl,x/z*512,y/z*512,512
					ShowEntity s\hudhl
					If z > 1500 Then 
						EntityAlpha s\hudhl,1-Float(z-1500)/500.0
					Else
						EntityAlpha s\hudhl,1
					EndIf
					EntityColor s\hudhl,255,0,0
					ScaleEntity s\hudhl,1,1,1
					If z<selz And s<>main_pl\target And Abs((x/z+1)-Float(mx)*2/main_width)<0.1 And Abs((1-y/z)-Float(my)*2/main_height)<0.1
						sels = s\hudhl
						selz = z
					EndIf
				Else
					HideEntity s\hudhl
				EndIf
			Else
				HideEntity s\hudhl
				If hud_needssupport Then
					If s\team = main_pl\team And s\spawntimer <= 0 And s\shc\supportrange <> 0 And s<>main_pl
						ShowEntity s\hudhl
						EntityColor s\hudhl,200,170,10
						ScaleEntity s\hudhl,2,2,1
						dist# = EntityDistance(s\piv,main_pl\piv)
						If z > 2000 Then 
							EntityAlpha s\hudhl,1-Float(z-2000)/1000.0
						Else
							EntityAlpha s\hudhl,1
						EndIf
						PositionEntity s\hudhl,EntityX(s\piv,1), EntityY(s\piv,1), EntityZ(s\piv,1), 1
						MoveEntity s\hudhl,0,0,dist/2.0
						
						If dist < supportdist
							supportdist = dist
							support = s
						EndIf
					EndIf
				EndIf
			EndIf
		Next
		If sels <> 0 Then ScaleEntity sels,2,2,1
		
		HideEntity hud_nexttpointer
		HideEntity hud_nextta
		
		If support<>Null And hud_needssupport=1 Then 
			dy# = DeltaYaw(cc_cam,support\piv)
			dp# = DeltaPitch(cc_cam,support\piv)
			tdist# = Sqr(dy#^2+dp#^2)
			If tdist>=15 Then
				ShowEntity hud_nexttpointer
				EntityColor hud_nexttpointer,200,170,10
				twinkel# = ((ATan2(-dy,dp)+360) Mod 360) - EntityRoll(cc_cam,1)
				RotateEntity hud_nexttpointer,0,0,twinkel+180
			Else
				HideEntity hud_nexttpointer
			EndIf
		EndIf
		
		If main_pl\target.ship <> Null
			dist#	= EntityDistance(main_pl\piv,main_pl\target\piv)
			ShowEntity hud_selected
			ShowEntity hud_selected2
			CameraProject cc_cam,EntityX(main_pl\target\piv),EntityY(main_pl\target\piv),EntityZ(main_pl\target\piv)
			x# = ProjectedX()
			y# = ProjectedY()
			z# = ProjectedZ()
			If z>0 And x>0 And x<main_width And y>0 And y<main_height
				EntityParent hud_selected,hud_piv
				Util_Approach(hud_selected,x*1024/main_width,-y*2*hud_height/main_height-15,0,.5)
				Util_Approach(hud_selected2,0,20,0)
				HideEntity hud_tmini
			Else
				EntityParent hud_selected,hud_piv
				Util_Approach(hud_selected,100,-hud_height*2+100,0,.5)
				Util_Approach(hud_selected2,0,-44,0)
				ShowEntity hud_tmini
				hud_logshift = 1
			EndIf
			
			hud_thealth\value2 = main_pl\target\shc\Hitpoints
			hud_thealth\value = main_pl\target\hitpoints
			
			hud_tshields\value2 = main_pl\target\shc\Shields
			hud_tshields\value = main_pl\target\shields
			
			hud_tname = Txt_UpdateText(hud_tname, main_pl\target\shc\name, hud_font, hud_selected2)
			PositionEntity hud_tname,-44,44,0
			EntityOrder hud_tname,-12
			
			hud_tname2 = Txt_UpdateText(hud_tname2, main_pl\target\name, hud_font, hud_selected2)
			PositionEntity hud_tname2,-44,33,0
			EntityOrder hud_tname2,-12
			
			FreeEntity hud_tdist
			hud_tdist = Txt_Text(Int(dist), hud_font, hud_selected)
			PositionEntity hud_tdist,-41,-55,0
			ScaleEntity hud_tdist,1.2,1.2,1.2
			EntityOrder hud_tdist,-12
			
			dy# = DeltaYaw(cc_cam,main_pl\target\piv)
			dp# = DeltaPitch(cc_cam,main_pl\target\piv)
			tdist# = Sqr(dy#^2+dp#^2)
			If tdist>=25 Then
				ShowEntity hud_tshow
				twinkel# = ((ATan2(-dy,dp)+360) Mod 360) - EntityRoll(cc_cam,1)
				RotateEntity hud_tshow,0,0,twinkel+180
			Else
				HideEntity hud_tshow
			EndIf
						
			EntityParent hud_tpiv,main_pl\target\piv,0
			PositionEntity hud_tpiv,0,0,0
			RotateEntity hud_tpiv,0,0,0
			
			TFormPoint EntityX(hud_tpiv,1),EntityY(hud_tpiv,1),EntityZ(hud_tpiv,1),0,cc_cam
			x#	= TFormedX()
			y#	= TFormedY()
			z#	= TFormedZ()
			If z > 0
				PositionEntity hud_aim2,x/z*512,y/z*512,512
				PointEntity hud_aim2,cc_cam
				ShowEntity hud_aim2
			Else
				HideEntity hud_aim2
			EndIf
			EntityParent hud_tpiv,0
			
			distt#	= dist# / (weaponid[main_pl\weapgroup[1]]\speed#+main_pl\frontspeed*1.1)
			TranslateEntity hud_tpiv,main_pl\target\dx*distt#,main_pl\target\dy*distt#,main_pl\target\dz*distt#,1
			TFormPoint EntityX(hud_tpiv,1),EntityY(hud_tpiv,1),EntityZ(hud_tpiv,1),0,cc_cam
			x#	= TFormedX()
			y#	= TFormedY()
			z#	= TFormedZ()
			If z > 0
				PositionEntity hud_aim,x/z*512,y/z*512,512
				PointEntity hud_aim,cc_cam
				ShowEntity hud_aim
			Else 
				HideEntity hud_aim
			EndIf
			
			If main_pl\targeting Then
				dy# = DeltaYaw(main_pl\targetlock,main_pl\target\piv)
				dp# = DeltaPitch(main_pl\targetlock,main_pl\target\piv)
				
				lockfact# = (Abs(dy)+Abs(dp))/180.0
				
				If lockfact < 0.6 Then
					ShowEntity hud_targetlock
					PositionEntity hud_targetlock, EntityX(main_pl\targetlock,1), EntityY(main_pl\targetlock,1), EntityZ(main_pl\targetlock,1) , 1
					RotateEntity hud_targetlock, EntityPitch(main_pl\targetlock,1), EntityYaw(main_pl\targetlock,1), EntityRoll(hud_targetlock,1) , 1
					If main_pl\targeting < 100 Then
						RotateSprite hud_targetlock,lockfact*420.0
						EntityAlpha hud_targetlock,1
					Else
						RotateSprite hud_targetlock,0
						If (MilliSecs()/500) Mod 2 = 1 Then EntityAlpha hud_targetlock,0.4 Else EntityAlpha hud_targetlock,1
					EndIf
					MoveEntity hud_targetlock, 0,0,EntityDistance(main_pl\targetlock, main_pl\target\piv)*(1.0-lockfact)
					Local si# = 6+EntityDistance(hud_targetlock, cc_cam)/60
					ScaleSprite hud_targetlock,si, si
				Else
					HideEntity hud_targetlock
				EndIf
			Else
				HideEntity hud_targetlock
			EndIf
		Else
			HideEntity hud_selected
			HideEntity hud_aim
			HideEntity hud_aim2
			HideEntity hud_tshow 
			HideEntity hud_targetlock
		EndIf
	Case 1
		Hud_UpdateBigMap()
		If main_pl = teamid[main_pl\team]\commander And cc_mode<>4 Then
			Hud_UpdateCommander()
		Else
			HideEntity hud_compiv
		EndIf
		GUI_Update()
		HUD_SelectScreen()
	Case 2
		GUI_UpdateGadget(hud_continue)
		If gkey<>"" Or gui_event=401 Then
			GUI_SetGadgetActivity(hud_continue,-1)
			gui_event = 0
			HUD_SetMode(1)
		EndIf
	Case 4
		If (gkey<>"" Or gui_event=401 Or net_isserver=1) And Game_RestartTimer < MilliSecs() Then
			GUI_SetGadgetActivity(hud_continue,-1)
			gui_event = 0
			Game_StartRound()
		ElseIf Game_RestartTimer-2000 < MilliSecs() Then
			Game_Pause = 1
			bloom_mb = .9
			bloom_mbmode = 1
			ShowEntity hud_end
			If Game_RestartTimer < MilliSecs() Then
				If hud_continue\active <> 1 Then GUI_SetGadgetActivity(hud_continue,1)
				gui_event = 0
				GUI_UpdateGadget(hud_continue)
			EndIf
		EndIf
	Case 5
		Hud_UpdateBigMap()
		For is.IShip = Each iship
			If is\hp <> 0 Then
				FreeEntity is\hp
				is\hp = 0
			EndIf
			If is\s\spawntimer <= 0 Then
				is\hp = Txt_Text(Int(100*is\s\hitpoints/is\s\shc\hitpoints), hud_font, is\name)
				MoveEntity is\hp,60,0,0
				EntityOrder is\hp ,-11
				EntityColor is\hp,is\s\colr,is\s\colg,is\s\colb
				EntityAlpha is\name,1
			Else
				EntityAlpha is\name,.5
			EndIf
		Next
		If main_pl = teamid[main_pl\team]\commander Then Hud_UpdateCommander()
	End Select
	
	If hud_mode < 20000
		FreeEntity hud_tickets1
		hud_tickets1 = Txt_Text(Int(teamid[1]\tickets), hud_font, hud_score)
		ScaleEntity hud_tickets1,2,2,2
		PositionEntity hud_tickets1,-36,0,0
		EntityOrder hud_tickets1,-14
		If teamid[1]\ticketlost > 0 Then 
			teamid[1]\ticketlost = teamid[1]\ticketlost - 10*main_gspe
			EntityColor hud_tickets1, 255, 0, 0
		EndIf
		
		FreeEntity hud_tickets2
		hud_tickets2 = Txt_Text(Int(teamid[2]\tickets), hud_font, hud_score)
		ScaleEntity hud_tickets2,2,2,2
		PositionEntity hud_tickets2,20,0,0
		EntityOrder hud_tickets2,-14
		If teamid[2]\ticketlost > 0 Then 
			teamid[2]\ticketlost = teamid[2]\ticketlost - 10*main_gspe
			EntityColor hud_tickets2, 255, 0, 0
		EndIf
	EndIf
End Function

Function HUD_RenderPlayer()
	HideEntity cc_cam
	ShowEntity hud_extracam
	EntityAlpha main_pl\mesh,1
	
	CameraViewport hud_extracam,785*main_width/1024,main_height-110*main_height/hud_height,190*main_width/1024,190*main_width/1024
	
	x# = EntityX(main_pl\mesh)
	y# = EntityY(main_pl\mesh)
	z# = EntityZ(main_pl\mesh)
	
	size# = Sqr(MeshDepth(main_pl\shc\mesh)^2+MeshWidth(main_pl\shc\mesh)^2+MeshHeight(main_pl\shc\mesh)^2)*5
	PositionEntity main_pl\mesh,0,64000-.5-size,0,1
	
	RenderWorld()
	
	PositionEntity main_pl\mesh,x,y,z
	
	HideEntity hud_extracam
	ShowEntity cc_cam
	EntityAlpha main_pl\mesh,1-(cc_mode=2)
End Function

Function HUD_SelectScreen()
	FreeEntity hud_countd
	hud_countd = Txt_Text(Int(Ceil(main_pl\spawntimer/1000.0)), hud_font, gui_par)
	ScaleEntity hud_countd,1.2,1.2,1
	PositionEntity hud_countd,-70,-40,0
	EntityOrder hud_countd,-14
	
	If main_pl\selspawn <> 0
		sp.spawn = Fla_FindSpawnByID(main_pl\selspawn)
		If sp <> Null
			If sp\f\team = main_pl\team Then
				hud_nospawn\vis = 0
			EndIf
		EndIf
	EndIf
	
	hud_stopspawn\vis = 1 - hud_nospawn\vis
	
	If MilliSecs() > hud_selectswitch And cc_mode <> 6 Then CC_SetTarget(0,6) : cc_spectating = 2	
	
	hud_showcsanim = util_minmax(hud_showcsanim + (hud_showcselect*8 - 4)*main_gspe,-50,0)
	PositionEntity gui_par, hud_showcsanim,0,100
	
	Select gui_event
	Case 401
		Select gui_eventsource
		Case hud_but[0]
			Team_JoinTeam(main_pl,1)
			Shi_SelectClass(Handle(main_pl),main_pl\selclass)
			HUD_SetSpawns(main_pl\selclass,main_pl\team)
		Case hud_but[1]
			Team_JoinTeam(main_pl,2)
			Shi_SelectClass(Handle(main_pl),main_pl\selclass)
			HUD_SetSpawns(main_pl\selclass,main_pl\team)
		Case hud_but[2]
			Shi_SelectClass(Handle(main_pl),1)
			HUD_SetSpawns(main_pl\selclass,main_pl\team)
			Shi_SendSpawnData(main_pl)
		Case hud_but[3]
			Shi_SelectClass(Handle(main_pl),2)
			HUD_SetSpawns(main_pl\selclass,main_pl\team)
			Shi_SendSpawnData(main_pl)
		Case hud_but[4]
			Shi_SelectClass(Handle(main_pl),3)
			HUD_SetSpawns(main_pl\selclass,main_pl\team)
			Shi_SendSpawnData(main_pl)
		Case hud_but[5]
			Shi_SelectClass(Handle(main_pl),4)
			HUD_SetSpawns(main_pl\selclass,main_pl\team)
			Shi_SendSpawnData(main_pl)
		Case hud_but[6]
			hud_showcselect = 1 - hud_showcselect
			GUI_SetTurn(hud_but[6],180*(1-hud_showcselect))
			If hud_showcselect Then
				HUD_SetSpawns(main_pl\selclass,main_pl\team)
			Else
				HUD_SetSpawns(0,100)
			EndIf
		Case Handle(hud_stopspawn)
			main_pl\selspawn = 0
			HUD_SetSpawns(main_pl\selclass,main_pl\team)
		End Select
	Default
		If mh = 1 And hud_showcselect
			c = 0
			For s.spawn = Each spawn
				If s\maps Then 
					CameraProject cc_cam,EntityX(s\maps,1), EntityY(s\maps,1), EntityZ(s\maps,1)
					If Sqr((mx-ProjectedX())^2 + (my-ProjectedY())^2) < 20
						main_pl\selspawn = s\id
						c = 1
					EndIf
				EndIf
			Next
			If c Then Shi_SendSpawnData(main_pl)
			
			For s.spawn = Each spawn
				If s\maps Then 
					Hud_MSize s\maps,100
					If main_pl\selspawn = s\id
						Hud_MSize s\maps,200
					EndIf
				EndIf
			Next
		EndIf
	End Select
	gui_event = 0
	gui_eventsource = 0
End Function

Function HUD_Retarget(typ=0,team=0)
	If main_pl\target.ship <> Null
		refdist# = EntityDistance(main_pl\piv,main_pl\target\piv)	
	Else
		refdist# = 0
	EndIf
	newdist# = 2000
	oldtarget = Handle(main_pl\target.ship)
	
	For s.ship = Each ship
		If (s\team <> main_pl\team Or team=1 Or (team=2 And s\team = main_pl\team)) And s\spawntimer <= 0 And (typ=0 Or typ=s\shc\typ) And s<>main_pl
			dist# = (EntityDistance(main_pl\piv,s\piv) + 2*Abs(DeltaYaw(main_pl\piv,s\piv) + DeltaPitch(main_pl\piv,s\piv)) ) * ((s\stealthed>0)*2+1)
			If dist <= 2000
				t = 0
				If typ = 0 And team = 0
					CameraProject cam_cam,EntityX(s\piv),EntityY(s\piv),EntityZ(s\piv)
					If ProjectedZ() > 0
						If (mx-ProjectedX())^2 + (my-ProjectedY())^2 < (50000.0/dist)^2
							dist = dist-2000
							t = 1
						EndIf
					EndIf
				EndIf
				If dist# < Newdist And (dist > refdist Or t = 1)
					newdist = dist
					newtarget = Handle(s.ship)
				EndIf
			EndIf
		EndIf
	Next
	
	If oldtarget = Newtarget Then
		main_pl\target.ship = Null
		HUD_ChangeTarget(0)
	Else
		PlaySound hud_button
		HUD_ChangeTarget(newtarget)
	EndIf
End Function

Function HUD_FindNextTarget(typ=0,team=0)
	refdist# = 0
	newdist# = 2000
	oldtarget = Handle(main_pl\target.ship)
	
	For s.ship = Each ship
		If (s\team <> main_pl\team Or team=1 Or (team=2 And s\team = main_pl\team)) And s\spawntimer <= 0 And (typ=0 Or typ=s\shc\typ) And s <> main_pl\target And s<>main_pl
			dist# = (EntityDistance(main_pl\piv,s\piv) + 2*Abs(DeltaYaw(main_pl\piv,s\piv) + DeltaPitch(main_pl\piv,s\piv)) ) * ((s\stealthed>0)*2+1)
			If dist <= 2000
				t = 0
				If typ = 0 And team = 0
					CameraProject cam_cam,EntityX(s\piv),EntityY(s\piv),EntityZ(s\piv)
					If ProjectedZ() > 0
						If (mx-ProjectedX())^2 + (my-ProjectedY())^2 < (10000.0/dist)^2
							dist = dist-2000
							t = 1
						EndIf
					EndIf
				EndIf
				If dist# < Newdist And (dist > refdist Or t = 1)
					newdist = dist
					newtarget = Handle(s.ship)
				EndIf
			EndIf
		EndIf
	Next
	
	Return newtarget
End Function

Function HUD_ChangeTarget(shandle)
	Shi_SetTarget(main_pl, Object.ship(shandle))
	If main_pl\target.ship <> Null
		If main_pl\target\team = main_pl\team
			EntityColor hud_selected,0,240,0
			EntityColor hud_selected2,0,240,0
			EntityColor hud_aim,0,240,0
			EntityColor hud_aim2,0,240,0
			EntityColor hud_tshow,0,240,0
		Else
			EntityColor hud_selected,255,0,0
			EntityColor hud_selected2,255,0,0
			EntityColor hud_aim,255,0,0
			EntityColor hud_aim2,255,0,0
			EntityColor hud_tshow,255,0,0
		EndIf
		
		EntityTexture hud_tmini, main_pl\target\shc\minitex
	EndIf
End Function

Function HUD_EndDialog()
	FreeEntity hud_end
	
	hud_end = LoadSprite(hudd$+"gameover.png",1+2,cc_cam)
	EntityAlpha hud_end,1
	EntityFX hud_end,1+8
	EntityBlend hud_end,1
	EntityOrder hud_end,-10
	PositionEntity hud_end,0,0,400
	ScaleSprite hud_end,300,300
	
	txt = Txt_Text(teamid[Game_Winner]\name, hud_font, hud_end)
	ScaleMesh txt,4,5,5
	PositionMesh txt,-MeshWidth(txt)/2,40,0
	EntityOrder txt,-14
	EntityColor txt,teamid[Game_Winner]\colr,teamid[Game_Winner]\colg,teamid[Game_Winner]\colb
	
	txt = Txt_Text(lang_win, hud_font, hud_end)
	ScaleMesh txt,2,2,2
	PositionMesh txt,-MeshWidth(txt)/2,15,0
	EntityOrder txt,-14
	
	txt = Txt_Text(Int(teamid[1]\tickets)+":"+Int(teamid[2]\tickets), hud_font, hud_end)
	ScaleMesh txt,3,3,3
	PositionMesh txt,-MeshWidth(txt)/2,-20,0
	EntityOrder txt,-14
	
	txt = Txt_Text(Int(teamid[1]\victories)+":"+Int(teamid[2]\victories), hud_font, hud_end)
	ScaleMesh txt,6,6,6
	PositionMesh txt,-MeshWidth(txt)/2,-85,0
	EntityOrder txt,-14
	
	HideEntity hud_end
End Function


Function HUD_OrderFlags()
	t = 1
	For f.flag = Each flag
		If f\team = 1 Then 
			PositionEntity hud_flags[f\num],-50+t*8,-6,0
			t = t + 1
		EndIf
		ShowEntity hud_flags[f\num]
	Next
	For f.flag = Each flag
		If f\team = 0 Then 
			PositionEntity hud_flags[f\num],-50+t*8,-6,0
			t = t + 1
		EndIf
	Next
	For f.flag = Each flag
		If f\team = 2 Then 
			PositionEntity hud_flags[f\num],-50+t*8,-6,0
			t = t + 1
		EndIf
	Next
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D