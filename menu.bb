
Type TMap
	Field path$
	Field name$
	Field sprite
	Field aura, pulse
	Field cmesh
End Type

Type TCredit
	Field mesh
	Field alpha#
End Type

Type TServer
	Field ip$
	Field name$
	Field port
	Field map$
	Field listid
End Type

Global menu_cam
Global menu_camshake
Global menu_target, menu_selmap.TMap

Global menu_win1.TGadget,menu_win2.TGadget,menu_win3.TGadget,menu_win4.TGadget,menu_win5.TGadget
Global menu_win6.TGadget,menu_win7.TGadget,menu_win8.TGadget,menu_win9.TGadget,menu_win10.TGadget

Global menu_inputname.TGadget
Global menu_gfx.TGadget[10]
Global menu_con.TGadget[25]
Global menu_serverlist.TGadget,menu_serverip.TGadget,menu_serverport.TGadget, menu_nsname.TGadget, menu_nsport.TGadget

Global menu_end=0
Global menu_credits=0
Global menu_options=0
Global menu_netgame=0
Global menu_foptions=0

Global menu_startgame=0
Global menu_gamemode=1

Global menu_failure$, menu_fmesh
Global menu_creditTimer, Menu_CreditString$, Menu_CPointer, Menu_CreditY
Global firststart = 0

Global menu_tcpstream, menu_udpstream

Function Menu_Start()
	menu_cam = CreateCamera()
	CameraRange menu_cam, 1,5000
	
	MGui_Init()
	
	If firststart = 1
		menu_keks = LoadSprite("GFX/MENU/cookie.png",1,menu_cam)
		EntityBlend menu_keks,1
		EntityAlpha menu_keks,0
		PositionEntity menu_keks,0,0,2
		For i = 0 To 255 Step 10
			Cls
			CameraClsColor menu_cam,i,i,i
			RenderWorld
			Flip
		Next
		For i = 0 To 255 Step 1
			Cls
			EntityAlpha menu_keks,Float(i)/255.0
			RenderWorld
			Flip
		Next
		Delay 500
		For i = 255 To 0 Step -10
			Cls
			EntityColor menu_keks,i,i,i
			CameraClsColor menu_cam,i,i,i
			RenderWorld
			Flip
		Next
		firststart = 1
		FreeEntity menu_keks
	EndIf
	
	Music_SpecialTrack("menu_start")
	
	CameraClsColor menu_cam,0,0,0
	
	If menu_failure$ <> ""
		menu_fmesh = CreateSprite(menu_cam)
		EntityBlend menu_fmesh,2
		EntityColor menu_fmesh,100,50,40
		ScaleSprite menu_fmesh,512,512 * main_hheight / main_hwidth
		PositionEntity menu_fmesh,0,0,512
		EntityOrder menu_fmesh,-200
		
		menu_ftext = Txt_Text(lang_menu_error+": "+menu_failure, MGui_Font, menu_fmesh,0,150)
		EntityOrder menu_ftext ,-201
		PositionEntity menu_ftext,0,0,0
		ScaleMesh menu_ftext,5,5,4
		PositionMesh menu_ftext,-MeshWidth(menu_ftext)/2,-MeshHeight(menu_ftext)/2,0
	EndIf
	
	menu_splash = Util_LoadSprite("GFX/MENU/splash.png",1,menu_cam)
	EntityBlend menu_splash,1
	ScaleEntity menu_splash,512,512 * main_hheight / main_hwidth,1
	PositionEntity menu_splash,0,0,512
	EntityOrder menu_splash,-100
	menu_splash_vis# = 1
	
	cam_cam = menu_cam
	
	RenderWorld()
	Flip
	
	env_sky = CreatePivot()
	Map_Load("GFX/MENU/menu.amap")
	
	dir = ReadDir("Maps")
	Repeat
		file$ = NextFile(dir)
		If Right(file$,5) = ".amap" Then
			m.TMap = New TMap
			m\path = file$
		EndIf
	Until file$ = ""
	CloseDir dir
	
	Menu_BuildInterface()
	
	Util_InitTimer()
	Mouse_init(menu_cam)
	
	menu_end = 0
	
	g.tgadget=MGui_CreateWindow.TGadget(150,100*main_ratio-30,40,41, "", Null)
		NetGame.Tgadget = MGui_CreateButton.TGadget(1,1,38,6, "netgame", 100,100,100, g ,"netgame")
			NetGame\anim = 60
		Optionen.Tgadget = MGui_CreateButton.TGadget(1,9,38,6, "options", 100,100,100, g ,"options")
			optionen\anim = 90
		 Credits.Tgadget = MGui_CreateButton.TGadget(1,17,38,6, "credits", 100,100,100, g ,"credits")
			credits\anim = 120
		 Beenden.Tgadget = MGui_CreateButton.TGadget(1,26,38,6, "close"  , 100,100,100, g ,"end")
			beenden\anim = 150
		  online.Tgadget = MGui_CreateButton.TGadget(1,35 ,38,5, "mrkeks.net", 100,100,100, g ,"mrkeks")
			online\anim = 180
	menu_win1.TGadget = g
	
	g.tgadget=MGui_CreateWindow.TGadget(60,200*main_ratio-50,80,17, "", Null)
		g\hide = 1
		g\anim = 100
		back.Tgadget = MGui_CreateButton.TGadget(1,1,39,8, "back", 255,200,100, g ,"ms_back")
			back\anim = 100
			back\hide = 1
		play.Tgadget = MGui_CreateButton.TGadget(40,1,39,8, "play", 100,200,255, g ,"ms_play")
			play\anim = 100
			play\hide = 1
		pcount.TGadget = MGui_CreateCombo.TGadget(40,9,39,6, "players", g , "ms_pcount")
			MGui_AddItem(pcount,"8")
			MGui_AddItem(pcount,"16")
			MGui_AddItem(pcount,"24")
			MGui_AddItem(pcount,"32")
			MGui_AddItem(pcount,"40")
			MGui_AddItem(pcount,"48")
			MGui_SelectItem(pcount,2)
	menu_win2.TGadget = g
	
	g.tgadget=MGui_CreateWindow.TGadget(5,200*main_ratio-10,41,8, "", Null)
		g\hide = 1
		g\anim = 100
		cback.Tgadget = MGui_CreateButton.TGadget(1,1,39,6, "back", 255,200,100, g ,"cr_back")
			cback\anim = 100
			cback\hide = 1
	menu_win3.TGadget = g
	
	g.tgadget=MGui_CreateWindow.TGadget(74,100*main_ratio-29,52,58, "", Null)
		g\hide = 1
		g\anim = 100
		graph.Tgadget = MGui_CreateButton.TGadget(1,1,50,6, "graphics", 100,100,100, g ,"op_graphics")
			graph\anim = 100
			graph\hide = 1
		contr.Tgadget = MGui_CreateButton.TGadget(1,9,50,6, "controls", 100,100,100, g ,"op_controls")
			contr\anim = 100
			contr\hide = 1
		
		clang.TGadget = MGui_CreateCombo.TGadget(1,20,50,6, "language", g , "op_sellang")
			MGui_AddItem(clang,"de")
			MGui_AddItem(clang,"en")
			MGui_SelectItem(clang,1+("en_"=main_lang))
				
		pname.TGadget = MGui_CreateInput.TGadget(1,31,50,6, "name", main_name, g ,"op_setname")
			pname\anim = 100
			pname\hide = 1
			menu_inputname = pname
		
		pport.TGadget = MGui_CreateInput.TGadget(1,42,50,6, "port", main_port, g ,"op_setport",imNum)
			pport\anim = 100
			pport\hide = 1
		
		
		oback.Tgadget = MGui_CreateButton.TGadget(1,50,25,6, "back", 255,200,100, g ,"op_back")
			oback\anim = 100
			oback\hide = 1
	menu_win4.TGadget = g
	
	
	g.tgadget=MGui_CreateWindow.TGadget(74,100*main_ratio-40,52,78, "", Null)
		g\hide = 1
		g\anim = 100
		
		gfxmode.TGadget = MGui_CreateCombo.TGadget(1,1,50,5, "resolution", g , "og_setres")
		MGui_SetHint(gfxmode,"Choose your favorite screen resolution. Higher resolutions will have negative effects on the performance.", "Wähle deine Auflösung. Hohe Auflösungen werden die Performance negativ beeinflussen.")
			N = CountGfxModes()
			For i = 1 To N
				If GfxModeWidth(i)>=800 Then 
					i2 = i2 + 1
					MGui_AddItem(gfxmode,GfxModeWidth(i)+"x"+GfxModeHeight(i)+"x"+GfxModeDepth(i))
					If GfxModeWidth(i)=main_width And GfxModeHeight(i)=main_height And GfxModeDepth(i)=main_bit
						MGui_SelectItem(gfxmode,i2)
					EndIf
				EndIf
			Next
			
		windowed.TGadget = MGui_CreateTick.TGadget(1,8,5,5, "windowed", (main_mode=2), g, "og_windowed")
		MGui_SetHint(windowed,"Should the game be displayed in windowed mode? (Otherwise: Fullscreen)","Lässt das Programm im Fenstermodus anzeigen, sonst Vollbild.")
		
		vsync.TGadget = MGui_CreateTick.TGadget(1,14,5,5, "vsync", (main_vsync=1), g, "og_vsync")
		MGui_SetHint(vsync,"Vertical synchronization avoids screen tearing.", "Vertikale Synchronisation verhindert zerstückelte Frames.")
		
		antia.TGadget = MGui_CreateTick.TGadget(1,20,5,5, "antialiasing", (main_aalias), g, "og_antia")
		MGui_SetHint(antia,"Antialiasing will make the edges look smoother, but costs quite some performance.","Antialiazsing glättet Kanten im Bild, kostet aber Rechenleistung.")
		
		texd.TGadget = MGui_CreateTick.TGadget(1,26,5,5, "high texture detail", (main_texdetail=2), g, "og_texd")
		MGui_SetHint(texd,"Only deaktivate this if you are experiencing severe rendering problems!", "Nur deaktivieren, wenn die Grafik komplett spinnt!")
		
		detail.TGadget = MGui_CreateSlider.TGadget(1,37,50,2.5, "details", util_minmax(main_detail * 2,0,4), 4, g , "og_detail")
		MGui_SetHint(detail,"Choos the level of detail for particle effects, background imagery etc.","Wähle den Detailgrad für Partikeleffekte, Hintergrundbilder etc.")
		
		bloom.TGadget = MGui_CreateSlider.TGadget(1,46,50,2.5, "bloomfilter", util_minmax(main_bloom * 4,0,4), 4, g, "og_bloom")
		MGui_SetHint(bloom,"Improves light effects (looks like poor HDR-Rendering), but will slow down the game extremely.","Verbessert Lichteffekte, sieht wie schlechtes HDR aus. Warnung: Halbiert die Performance!")
		
		ogback.Tgadget = MGui_CreateButton.TGadget(1,71,24,6, "back", 255,200,100, g ,"og_back")
			ogback\anim = 100
			ogback\hide = 1
		ogaccept.Tgadget = MGui_CreateButton.TGadget(26,71,25,6, "save", 100,200,255, g ,"og_save")
			ogaccept\anim = 100
			ogaccept\hide = 1
			
		menu_gfx[0] = windowed
		menu_gfx[1] = vsync
		menu_gfx[2] = antia
		menu_gfx[3] = bloom
		menu_gfx[4] = texd
		menu_gfx[5] = gfxmode
		menu_gfx[6] = detail
	menu_win5.TGadget = g
	
	g.tgadget=MGui_CreateWindow.TGadget(49,100*main_ratio-45,103,87, "", Null)
		g\hide = 1
		g\anim = 100
		
		menu_con[20] = MGui_CreateButton.TGadget(0,-6,28,6, "profile1", 255,255,255, g ,"oc_profile1")
			menu_con[20]\anim = 100
			menu_con[20]\hide = 1
			If cc_profile <> 0 Then MGui_GadgetColor(menu_con[20], 100,100,100)
			
		menu_con[21] = MGui_CreateButton.TGadget(29,-6,28,6, "profile2", 255,255,255, g ,"oc_profile2")
			menu_con[21]\anim = 100
			menu_con[21]\hide = 1
			If cc_profile <> 1 Then MGui_GadgetColor(menu_con[21], 100,100,100)
		
		menu_con[0] = MGui_CreateKeySelect.TGadget(1,1,50,6, "accelerate", cc_accelerate, g, "")
		menu_con[1] = MGui_CreateKeySelect.TGadget(1,8,50,6, "decelerate", cc_decelerate, g, "")
		menu_con[2] = MGui_CreateKeySelect.TGadget(1,15,50,6, "slide left", cc_slideleft, g, "")
		menu_con[3] = MGui_CreateKeySelect.TGadget(1,22,50,6, "slide right", cc_slideright, g, "")
		menu_con[4] = MGui_CreateKeySelect.TGadget(1,29,50,6, "roll ccw", cc_rollleft, g, "")
		menu_con[5] = MGui_CreateKeySelect.TGadget(1,36,50,6, "roll cw", cc_rollright, g, "")
		menu_con[6] = MGui_CreateKeySelect.TGadget(1,43,50,6, "afterburner", cc_afterburner, g, "")
		
		menu_con[7] = MGui_CreateKeySelect.TGadget(1,53,50,6, "primary", cc_fireprimary, g, "")
		menu_con[8] = MGui_CreateKeySelect.TGadget(1,60,50,6, "secondary", cc_firesecondary, g, "")
		menu_con[20] = MGui_CreateKeySelect.TGadget(1,67,50,6, "tertiary", cc_specialweapon, g, "")
		
		menu_con[9] = MGui_CreateKeySelect.TGadget(52,1,50,6, "retarget", cc_retarget, g, "")
		menu_con[10] = MGui_CreateKeySelect.TGadget(56,8,46,6, "fighter", cc_retargetfighter, g, "")
		menu_con[11] = MGui_CreateKeySelect.TGadget(56,15,46,6, "bomber", cc_retargetbomber, g, "")
		menu_con[12] = MGui_CreateKeySelect.TGadget(56,22,46,6, "cruiser", cc_retargetbigship, g, "")
		menu_con[13] = MGui_CreateKeySelect.TGadget(56,29,46,6, "friend", cc_retargetfriend, g, "")
		menu_con[14] = MGui_CreateKeySelect.TGadget(56,36,46,6, "any", cc_retargetall, g, "")
		menu_con[15] = MGui_CreateKeySelect.TGadget(52,43,50,6, "adapt speed", cc_targetspeed, g, "")
		
		menu_con[16] = MGui_CreateKeySelect.TGadget(52,52,50,6, "change view", cc_camerachangetarget, g, "")
		menu_con[17] = MGui_CreateKeySelect.TGadget(52,59,50,6, "zoom in", cc_zoomin, g, "")
		
		menu_con[19] = MGui_CreateCombo.TGadget(52,67,50,6, "device", g , "")
			MGui_AddItem(menu_con[19],"mouse")
			MGui_AddItem(menu_con[19],"joystick")
			MGui_SelectItem(menu_con[19],cc_joystick+1)
			menu_con[19]\anim = 100
			menu_con[19]\hide = 1
		
		ocback.Tgadget = MGui_CreateButton.TGadget(1,80,24,6, "back", 255,200,100, g ,"oc_back")
			ocback\anim = 100
			ocback\hide = 1
		ocaccept.Tgadget = MGui_CreateButton.TGadget(26,80,25,6, "save", 100,200,255, g ,"oc_save")
			ocaccept\anim = 100
			ocaccept\hide = 1
		
		For i = 0 To 17
			menu_con[i]\hide = 1
			menu_con[i]\anim = 100
		Next
	menu_win6.TGadget = g

	
	g.tgadget=MGui_CreateWindow.TGadget(60,200*main_ratio-20,102,10, "", Null)
		g\hide = 1
		g\anim = 100
		cback.Tgadget = MGui_CreateButton.TGadget(1,1,100,8, "You have to restart the game", 255,200,100, g ,"restart")
			cback\anim = 100
			cback\hide = 1
	menu_win7.TGadget = g
	
	
	g.tgadget=MGui_CreateWindow.TGadget(49,100*main_ratio-44,124,88, "", Null)
		g\hide = 1
		g\anim = 100
		
		njoin.Tgadget = MGui_CreateButton.TGadget(1,4,30,6, "join", 100,200,255, g ,"net_join")
			njoin\anim = 100
			njoin\hide = 1
		
		nrefresh.Tgadget = MGui_CreateButton.TGadget(1,11,30,6, "refresh", 100,100,100, g ,"net_refresh")
			nrefresh\anim = 100
			nrefresh\hide = 1
			
		ncreate.Tgadget = MGui_CreateButton.TGadget(1,21,30,6, "create", 100,100,100, g ,"net_create")
			ncreate\anim = 100
			ncreate\hide = 1
		
		nback.Tgadget = MGui_CreateButton.TGadget(1,31,30,6, "back", 255,200,100, g ,"net_back")
			nback\anim = 100
			nback\hide = 1
		
		nip.TGadget = MGui_CreateInput.TGadget(32,4,60,6, "serverip", "0.0.0.0", g ,"",imNumIP)
			nip\anim = 100
			nip\hide = 1
			menu_serverip = nip
		nport.TGadget = MGui_CreateInput.TGadget(93,4,30,6, "serverport", main_port, g ,"",imNum)
			nport\anim = 100
			nport\hide = 1
			menu_serverport = nport
		
		slist.TGadget = MGui_CreateList.TGadget(32,11,91,76,g ,"net_selectserver", "net_join")
			slist\anim = 100
			slist\hide = 1
			menu_serverlist = slist
	menu_win8.TGadget = g
	
	
	g.tgadget=MGui_CreateWindow.TGadget(60,100*main_ratio-27,91,55, "", Null)
		g\hide = 1
		g\anim = 100
		
		nscreate.Tgadget = MGui_CreateButton.TGadget(1,4,25,6, "create", 100,200,255, g ,"net_screate")
			nscreate\anim = 100
			nscreate\hide = 1
		
		nsback.Tgadget = MGui_CreateButton.TGadget(1,47,25,6, "back", 255,200,100, g ,"net_sback")
			nsback\anim = 100
			nsback\hide = 1
		
		If Right(main_name,1) = "s" Then 
			tsname$ = main_name+"' Server"
		Else
			If main_lang="de_" Then tsname$ = main_name+"s Server" Else tsname$ = main_name+"'s Server"
		EndIf
		nsname.TGadget = MGui_CreateInput.TGadget(27,4,62,6, "servername", main_name+"'s Server", g ,"")
			nsname\anim = 100
			nsname\hide = 1
			menu_nsname = nsname
		nsplayers.TGadget = MGui_CreateInput.TGadget(27,15,25,6, "players", "6", g ,"",imNum)
			nsplayers\anim = 100
			nsplayers\hide = 1
		npcount.TGadget = MGui_CreateCombo.TGadget(53,15,25,6, "bots", g , "")
			MGui_AddItem(npcount,"0")
			MGui_AddItem(npcount,"8")
			MGui_AddItem(npcount,"16")
			MGui_AddItem(npcount,"24")
			MGui_AddItem(npcount,"32")
			MGui_SelectItem(npcount,2)
			npcount\anim = 100
			npcount\hide = 1
		nsmap.TGadget = MGui_CreateCombo.TGadget(27,25,51,6, "map", g , "")
			nsmap\anim = 100
			nsmap\hide = 1
		nshide.TGadget = MGui_CreateTick.TGadget(27,35,5,5, "hide", 0, g, "")
		nsport.TGadget = MGui_CreateInput.TGadget(27,47,30,6, "serverport", main_port, g ,"")
			nsport\anim = 100
			nsport\hide = 1
			menu_nsport = nsport
	menu_win9.TGadget = g
	
	If main_name = "player"
		g.tgadget=MGui_CreateWindow.TGadget(60,100*main_ratio-15,82,28, "", Null)
			g\anim = 100
			
			clang.TGadget = MGui_CreateCombo.TGadget(1,1,80,6, "language", g , "op_sellang")
				MGui_AddItem(clang,"de")
				MGui_AddItem(clang,"en")
				MGui_SelectItem(clang,1+("en_"=main_lang))
					
			pname.TGadget = MGui_CreateInput.TGadget(1,13,80,6, "please enter your name!", "", g ,"op_setname")
				pname\anim = 100
			
			
			oback.Tgadget = MGui_CreateButton.TGadget(54,21,25,6, "continue", 100,200,255, g ,"op_restart")
				oback\anim = 100
		menu_win10.TGadget = g
		menu_foptions = 1
		menu_win1\hide = 1
	Else
		menu_foptions = 0
	EndIf
	
	For m.TMap = Each TMap
		stream = ReadFile("Maps/"+m\path)
		name$ = "No name"
		Repeat
			lin$ = Trim(ReadLine(stream))
			If Util_GetParas(lin$)
				Select Lower(paras[0])
					Case "mapicon"
						m\sprite	= Util_LoadSprite(paras[1],1+2)
					Case "posx"
						x#			= paras[1]
					Case "posy"
						y#			= paras[1]
					Case "posz"
						z#			= paras[1]
					Case "name",main_lang+"name"
						name$		= paras[1]
				End Select
			EndIf
		Until lin="{"
		CloseFile stream
		
		If m\sprite = 0 Then m\sprite = Util_LoadSprite("maps\noimage.png",1+2)
		m\aura = Util_LoadSprite("GFX/GUI/aura.png",1+2,m\sprite)
		EntityBlend m\aura,3
		PositionEntity m\aura,0,0,.2
		ScaleEntity m\aura,4,4,1
		EntityAlpha m\aura,.5
		
		ScaleMesh m\sprite,5,5,1
		PositionEntity m\sprite,x,y,z
		
		m\cmesh = Txt_Text(name$, MGui_Font, m\sprite)
		EntityOrder m\cmesh ,-4
		PositionEntity m\cmesh,0,-3,0
		ScaleMesh m\cmesh,.6,.6,.6
		PositionMesh m\cmesh,-MeshWidth(m\cmesh)/2,-MeshHeight(m\cmesh)/2,0
		
		m\pulse = Rand(360)
		
		m\name = name
		MGui_AddItem(nsmap,name)
	Next
	
	MGui_SelectItem(nsmap,1)
	
	Menu_CreditString = Menu_CreateCredits()
	Menu_CPointer = 1
	Menu_CreditY = 20
	
	menu_logo = Util_LoadSprite("GFX/MENU/logo.png",1+2,menu_cam)
	PositionEntity menu_logo,-15.5,16.0*main_ratio-2.75,20
	ScaleEntity menu_logo,5.5,5.5,1
	EntityAlpha menu_logo,.9
	EntityOrder menu_logo,-20
	
	menu_version = Txt_Text(main_version, MGui_Font, menu_logo)
	EntityOrder menu_version ,-21
	PositionEntity menu_version,0,-1,0
	ScaleMesh menu_version,.015,.015,.015
	PositionMesh menu_version,-MeshWidth(menu_version)/2,0,0
	
	FlushKeys
	
	Repeat
		Cls
		Util_Timer()
		
		If menu_splash_vis > 0 Then menu_splash_vis = menu_splash_vis - .015 * main_gspe
		EntityAlpha menu_splash,menu_splash_vis
		
		Music_Update()
		
		If menu_failure <> ""
			For i = 1 To 262
				If Inp_KeyHit(i) Then
					FreeEntity menu_fmesh
					menu_failure = ""
					Exit
				EndIf
			Next
		Else
			For m.TMap = Each TMap
				f# = .2+EntityDistance(m\cmesh,cam_cam)/130.0
				ScaleEntity m\cmesh,f,f,f
				EntityAlpha m\cmesh,.4
				If menu_selmap = Null And menu_options+menu_foptions = 0
					CameraProject menu_cam,EntityX(m\sprite),EntityY(m\sprite),EntityZ(m\sprite)
					If (mx-ProjectedX())^2 + (my-ProjectedY())^2 < 1000 Then
						EntityAlpha m\aura,1
						ScaleEntity m\aura,10,10,1
						EntityAlpha m\cmesh,1
						If mh Then
							PlaySound MGui_Click
							menu_target = m\sprite
							menu_selmap = m
							menu_win1\hide = 1
							menu_win2\hide = 0
						EndIf
					Else
						EntityAlpha m\aura,1-(Sin(m\pulse)/15.0+.4)
						ScaleEntity m\aura,(Sin(m\pulse)/15.0+.4)*15,(Sin(m\pulse+10)/15.0+.4)*16,1
						m\pulse = m\pulse + 2
					EndIf
				ElseIf menu_selmap = m
					EntityAlpha m\aura,(Sin(m\pulse)/15.0+.7)
					ScaleEntity m\aura,(Sin(m\pulse)/15.0+.7)*15,(Sin(m\pulse+10)/15.0+.7)*16,1
					m\pulse = m\pulse + 2
					EntityAlpha m\cmesh,1
				EndIf
			Next
			
			Menu_Camera()
			Mouse_Update()
			If menu_credits Then Menu_UpdateCredits()
		EndIf
		
		MGui_Update()
		If menu_udpstream Then Menu_Online()
		
		RenderWorld()
		
		If menu_startgame
			EntityAlpha menu_splash,1
			RenderWorld()
			Flip
			Select menu_gamemode
			Case 1 ; single
				path$ = menu_selmap\path
				c.TCombo = Object.TCombo(pcount\handl)
				players = c\seltext
				server$ = ""
			Case 2 ; server
				c.TCombo = Object.TCombo(nsmap\handl)
				tpath$ = c\seltext
				For m.TMap = Each TMap
					If tpath$ = m\name
						path$ = m\path
						Exit
					EndIf
				Next
				c.TCombo = Object.TCombo(npcount\handl)
				players = c\seltext
				ib.TInput = Object.TInput(nsname\handl)
				server$ = ib\txt
				ib.TInput = Object.TInput(nsplayers\handl)
				UDP_MaxUserCount = ib\txt
				ib.TInput = Object.TInput(nsport\handl)
				net_port = ib\txt
				tb.TTick = Object.TTick(nshide\handl)
				hide = tb\set
			Case 3 ; client
				path$ = ""
				players = 0
				ib.TInput = Object.TInput(nip\handl)
				server$ = ib\txt
				ib.TInput = Object.TInput(nport\handl)
				net_port = ib\txt
			End Select
			Obj_Clear()
			Map_Clear()
			FreeEntity env_sky
			Menu_Clear()
			
			menu_failure$ = ""
				
			Repeat
				net_listid = 0
				If hide = 0 And menu_gamemode = 2 Then Net_AddToServerList(server$,net_port,tpath$)
				Main_Play(path,players,menu_gamemode,server$)
				If Main_MapChange<>"" Then
					Cls
					Text 0,main_height-20,"Mapchange"
					Flip
					If net = 1 And net_isserver = 0 Then Delay 2000
				EndIf
				path = Main_MapChange
			Until Main_MapChange=""
			menu_netgame = 0
			menu_startgame = 0
			Return
		EndIf
		
		Flip
	Until KeyHit(1) Or menu_end
	Obj_Clear()
	Map_Clear()
	FreeEntity env_sky
	Menu_Clear()
	Return 1
End Function

Function Menu_Camera()
	menu_camshake = menu_camshake + 1
	TurnEntity  menu_cam,Sin(menu_camshake*1)*.01,Sin(50+menu_camshake*1.4)*.01,Sin(menu_camshake*-.9)*.01
	
	If menu_target
		dx# = -EntityX(menu_cam)+EntityX(menu_target)
		dy# = -EntityY(menu_cam)+EntityY(menu_target)
		dz# = -EntityZ(menu_cam)+EntityZ(menu_target)
		AlignToVector menu_cam,dx,dy,dz,3,.15^(main_gspe#)
		dist# = EntityDistance(menu_cam,menu_target)
		Util_Approach(menu_cam,EntityX(menu_target)-dx*15.0/dist,EntityY(menu_target)-dy*15.0/dist,EntityZ(menu_target)-dz*15.0/dist,.15)
	ElseIf menu_credits
		AlignToVector menu_cam,0,-1,0,3,.15^(main_gspe#)
	ElseIf menu_netgame
		AlignToVector menu_cam,-1,0,0,3,.15^(main_gspe#)
	Else
		AlignToVector menu_cam,0,0,1,3,.15^(main_gspe#)
		Util_Approach(menu_cam,0,0,0,.15)
	EndIf
End Function

Function Menu_BuildInterface()
	
End Function

Function Menu_Clear()
	Menu_CreditsClear()
	For m.TMap = Each TMap
		FreeEntity m\sprite
		Delete m
	Next
	FreeEntity menu_cam
	MGUI_Clear()
	Map_Clear()
	If menu_udpstream Then CloseUDPStream menu_udpstream
	menu_udpstream = 0
	menu_target = 0
	menu_selmap = Null
	menu_credits = 0
	menu_options = 0
End Function

Function MGui_Event(SenderHandle,name$) ; meine billige event-verwaltung (=
	
	Select name$
	Case "netgame"
		menu_win1\hide = 1
		menu_win8\hide = 0
		menu_netgame = 1
		Menu_ClearServerList()
		menu_tcpstream = Net_GetServerList()
		menu_fmesh = CreateSprite(menu_cam)
		
		If main_name="player" Or (main_name="Mr.Keks" And main_mrkeks=0)
			menu_failure = lang_menu_player_name
			EntityBlend menu_fmesh,2
			EntityColor menu_fmesh,100,50,40
			ScaleSprite menu_fmesh,512,512 * main_hheight / main_hwidth
			PositionEntity menu_fmesh,0,0,512
			EntityOrder menu_fmesh,-200
			
			menu_ftext = Txt_Text(lang_menu_player_name, MGui_Font, menu_fmesh)
			EntityOrder menu_ftext ,-201
			PositionEntity menu_ftext,0,0,0
			ScaleMesh menu_ftext,5,5,4
			PositionMesh menu_ftext,-MeshWidth(menu_ftext)/2,-MeshHeight(menu_ftext)/2,0
		EndIf
	Case "credits"
		menu_win1\hide = 1
		menu_win3\hide = 0
		menu_credits = 1
	Case "options"
		menu_win1\hide = 1
		menu_win4\hide = 0
		menu_options = 1
	Case "mrkeks"
		ExecFile "http://www.mrkeks.net"
	Case "end"
		menu_end = 1
	Case "restart"
		menu_end = 1
		Main_Restart = 1
	Case "op_restart"
		If main_name <> "player" And main_name <> "" Then 
			menu_end = 1
			Main_Restart = 1
		EndIf
		
	Case "ms_back"
		menu_selmap = Null
		menu_target = 0
		menu_win1\hide = 0
		menu_win2\hide = 1
	Case "ms_play"
		menu_gamemode = 1
		menu_startgame = 1
		
	Case "cr_back"
		menu_win1\hide = 0
		menu_win3\hide = 1
		menu_credits = 0
		Menu_CreditsClear()
		
	Case "op_graphics"
		menu_win5\hide = 0
		menu_win4\hide = 1
		menu_options = 2
	Case "op_controls"
		menu_win6\hide = 0
		menu_win4\hide = 1
		menu_options = 2
	Case "op_back"
		menu_win1\hide = 0
		menu_win4\hide = 1
		menu_options = 0
	Case "op_sellang"	
		c.TCombo = Object.TCombo(SenderHandle)
		Main_SetLanguage(c\seltext)
		
	Case "op_setname"
		ib.TInput = Object.TInput(SenderHandle)
		main_name = ib\txt
		MGui_ChangeText(menu_inputname,main_name,0)
		Main_SaveSettings()
	Case "op_setport"
		ib.TInput = Object.TInput(SenderHandle)
		main_port = ib\txt
		Main_SaveSettings()
		
	Case "og_back"
		menu_win5\hide = 1
		menu_win4\hide = 0
		menu_options = 1
	Case "og_save"
		menu_win5\hide = 1
		menu_win4\hide = 0
		menu_options = 1
		
		c.TCombo = Object.TCombo(menu_gfx[5]\handl)
		txt$ = c\seltext
		Util_GetParas(txt,"x")
		restart = (paras[0] <> main_width)
		main_width = paras[0]
		Util_GetParas(paras[1],"x")
		restart = restart + (paras[0] <> main_height)
		main_height = paras[0]
		restart = restart + (paras[1] <> main_bit)
		main_bit = paras[1]
		
		t.TTick = Object.TTick(menu_gfx[0]\handl)
		restart = restart + (t\set+1 <> main_mode)
		main_mode = t\set+1
		
		t.TTick = Object.TTick(menu_gfx[1]\handl)
		main_vsync = t\set
		t.TTick = Object.TTick(menu_gfx[2]\handl)
		main_aalias = t\set
		
		t.TTick = Object.TTick(menu_gfx[4]\handl)
		main_texdetail = t\set+1
		main_dot3 = t\set
		
		s.TSlider = Object.TSlider(menu_gfx[6]\handl)
		main_detail = Float(s\pos)/2.0
		main_bgdetail = util_minmax(main_detail,0,1)
		main_particledetail = main_detail 
		If main_detail < 0.4 Then 
			main_dot3 = 0
			main_showminiplayer = 0
			main_showminimap = 0
		Else
			main_showminiplayer = 1
			main_showminimap = 1
		EndIf
		
		
		s.TSlider = Object.TSlider(menu_gfx[3]\handl)
		main_bloom = s\pos / 4.0
		
		Main_SaveGfx()
		
		If restart > 0 Then
			menu_end = 1
			Main_Restart = 1
		EndIf
		
	Case "oc_back"
		menu_win6\hide = 1
		menu_win4\hide = 0
		menu_options = 1
	Case "oc_save"
		menu_win6\hide = 1
		menu_win4\hide = 0
		menu_options = 1
		
		k.TKeySelect = Object.TKeySelect(menu_con[0]\handl)
		cc_accelerate = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[1]\handl)
		cc_decelerate = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[2]\handl)
		cc_slideleft = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[3]\handl)
		cc_slideright = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[4]\handl)
		cc_rollleft = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[5]\handl)
		cc_rollright = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[6]\handl)
		cc_afterburner = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[7]\handl)
		cc_fireprimary = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[8]\handl)
		cc_firesecondary = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[9]\handl)
		cc_retarget = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[10]\handl)
		cc_retargetfighter = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[11]\handl)
		cc_retargetbomber = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[12]\handl)
		cc_retargetbigship = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[13]\handl)
		cc_retargetfriend = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[14]\handl)
		cc_retargetall = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[15]\handl)
		cc_targetspeed = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[16]\handl)
		cc_camerachangetarget = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[17]\handl)
		cc_zoomin = k\key
		k.TKeySelect = Object.TKeySelect(menu_con[20]\handl)
		cc_specialweapon = k\key
		
		CC_SaveControls(cc_profile)
	Case "oc_profile1", "oc_profile2"
		Select name$
		Case "oc_profile1"
			cc_profile = 0
		Case "oc_profile2"
			cc_profile = 1
		End Select
		If cc_profile = 0 Then
			MGui_GadgetColor(menu_con[20], 255,255,255)
		Else
			MGui_GadgetColor(menu_con[20], 100,100,100) 
		EndIf
		If cc_profile = 1 Then
			MGui_GadgetColor(menu_con[21], 255,255,255)
		Else
			MGui_GadgetColor(menu_con[21], 100,100,100) 
		EndIf
		Menu_LoadControls()
		Main_SaveSettings()
	
	Case "net_back"
		menu_win1\hide = 0
		menu_win8\hide = 1
		menu_netgame = 0
		Menu_ClearServerList()
	Case "net_refresh"
		Menu_ClearServerList()
		menu_tcpstream = Net_GetServerList()
	Case "net_join"
		menu_gamemode = 3
		menu_startgame = 1
	Case "net_create"
		menu_win9\hide = 0
		menu_win8\hide = 1
		If Right(main_name,1) = "s" Then 
			tsname$ = main_name+"' Server"
		Else
			If main_lang="de_" Then tsname$ = main_name+"s Server" Else tsname$ = main_name+"'s Server"
		EndIf
		MGui_ChangeText(menu_nsname,tsname$)
		MGui_ChangeText(menu_nsport,main_port)
		
	Case "net_selectserver"
		l.TList = Object.TList(SenderHandle)
		Menu_SelectServer(l\sel)
	Case "net_screate"
		menu_gamemode = 2
		menu_startgame = 1
	Case "net_sback"
		menu_win9\hide = 1
		menu_win8\hide = 0
	End Select
End Function

Function Menu_AddServer(ip$,name$,port,map$)
	s.TServer	= New TServer
	s\ip	 	= ip
	s\name		= name
	s\port		= port
	s\map		= map
	s\listid	= MGui_AddItem(menu_serverlist,Util_Shorten$(name,25)+Chr(9)+4+"0/0 "+Util_Shorten$(map,15))
	MGui_SetItemAlpha(menu_serverlist, s\listid, 0.5)
End Function

Function Menu_Online()
	If menu_tcpstream <> 0
		If ReadAvail(menu_tcpstream)
			version$ = ReadLine(menu_tcpstream)
			If version > main_version_id Then
				RuntimeError lang_more_recent_version
			EndIf
			While Not Eof(menu_tcpstream)
				name$	= ReadLine(menu_tcpstream)
				ip$		= ReadLine(menu_tcpstream)
				port	= ReadLine(menu_tcpstream)
				map$	= ReadLine(menu_tcpstream)
				If name <> "" Then
					Menu_AddServer(ip$,name$,port,map$)
					UDP_PingAServer(ip$,port)
				EndIf
			Wend
			CloseTCPStream menu_tcpstream
			menu_tcpstream = 0
		EndIf
	End If
	While RecvUDPMsg(menu_udpstream)
		If ReadAvail(menu_udpstream)
			action = ReadByte(menu_udpstream)
			Select action
			Case UDP_PingServer
				ip$ = DottedIP(UDPMsgIP(menu_udpstream))
				port = UDPMsgPort(menu_udpstream)
				maxusers = ReadByte(menu_udpstream)
				users = ReadByte(menu_udpstream)
				map$ = ReadString(menu_udpstream)
				For s.TServer = Each TServer
					If s\ip = ip And s\port = port Then
						s\map = map
						MGui_SetItemCaption(menu_serverlist,s\listid,Util_Shorten$(s\name,25)+Chr(9)+4+users+"/"+maxusers+" "+Util_Shorten$(s\map,15))
					EndIf
				Next
			End Select
		EndIf
	Wend
End Function

Function Menu_SelectServer(id)
	For s.TServer = Each TServer
		If id = s\listid Then
			MGui_ChangeText(menu_serverip,s\ip)
			MGui_ChangeText(menu_serverport,s\port)
			Exit
		EndIf
	Next
End Function

Function Menu_ClearServerList()
	Delete Each TServer
	MGui_RemoveItems(menu_serverlist)
End Function

Function Menu_LoadControls()
	CC_LoadControls(datad$+"controls_"+cc_profile+".ini")
	
	MGui_SelectKey(menu_con[0], cc_accelerate)
	MGui_SelectKey(menu_con[1], cc_decelerate)
	MGui_SelectKey(menu_con[2], cc_slideleft)
	MGui_SelectKey(menu_con[3], cc_slideright)
	MGui_SelectKey(menu_con[4], cc_rollleft)
	MGui_SelectKey(menu_con[5], cc_rollright)
	MGui_SelectKey(menu_con[6], cc_afterburner)
	MGui_SelectKey(menu_con[7], cc_fireprimary)
	MGui_SelectKey(menu_con[8], cc_firesecondary)
	MGui_SelectKey(menu_con[9], cc_retarget)
	MGui_SelectKey(menu_con[10], cc_retargetfighter)
	MGui_SelectKey(menu_con[11], cc_retargetbomber)
	MGui_SelectKey(menu_con[12], cc_retargetbigship)
	MGui_SelectKey(menu_con[13], cc_retargetfriend)
	MGui_SelectKey(menu_con[14], cc_retargetall)
	MGui_SelectKey(menu_con[15], cc_targetspeed)
	MGui_SelectKey(menu_con[16], cc_camerachangetarget)
	MGui_SelectKey(menu_con[17], cc_zoomin)
	MGui_SelectKey(menu_con[20], cc_specialweapon)
	
	MGui_SelectItem(menu_con[19],cc_joystick+1)
End Function

Function Menu_CreateCredits$()
	If main_lang = "de_"
		s$ = 	  "DGX9142*2004-2009 von Mr.Keks#www.mrkeks.net+;"
		s$ = s$ + "Benjamin (Mr.Keks) Bisping*Programmierung#Interface-Design#Maps##Steffen (CdV) Altmeier*Grafik#Schiffsdesign#Maps#;"
		s$ = s$ + "DerHase*www.isolationshaft.de+Zusätzliche Modelle#Texturen##Sebastian Schell*www.sebastian-schell.de+Musik#;"
		s$ = s$ + "Programme, die sich als nützlich erwiesen:*Blitz3d#GIMP2.0#Cinema4d#gile[s]#UnrealSoftware BitmapFontWizzard#BigBug's B3d-Exporter For C4d#;"
		s$ = s$ + "Dank an*Peer#CodeMaster#INpac#todes\*#Jan_#;"
		s$ = s$ + "Grüße an*Feuerware#Fetze#UnrealSoftware#Deutsche BlitzBasic Community#All meine verloren gegangenen Grafiker#k.o.g.#Ninja Monkeys#;<"
	Else
		s$ = 	  "DGX9142*2004-2009 by Mr.Keks#www.mrkeks.net+;"
		s$ = s$ + "Benjamin (Mr.Keks) Bisping*Programming#Interface Design#Maps##Steffen (CdV) Altmeier*Graphics#Ship Design#Maps#;"
		s$ = s$ + "DerHase*www.isolationshaft.de+Additional Graphics#Textures##Sebastian Schell*www.sebastian-schell.de+Music#;"
		s$ = s$ + "Programs that turned out useful:*Blitz3d#GIMP2.0#Cinema4d#gile[s]#UnrealSoftware BitmapFontWizzard#BigBug's B3d-Exporter for C4d#;"
		s$ = s$ + "Thanks to*Peer#CodeMaster#INpac#todes\*#Jan_#;"
		s$ = s$ + "Greetings to*Feuerware#Fetze#UnrealSoftware#German BlitzBasic Community#All my lost graphic artists#k.o.g.#Ninja Monkeys#;<"
	EndIf
	Return s
End Function

Function Menu_UpdateCredits()
	If MilliSecs() > menu_creditTimer
		s$ = Menu_CreditString
		For i = Menu_CPointer To Len(s)
			ca$ = Mid(s,i,1)
			Select ca
			Case ";" ; screenende				
				menu_creditTimer = MilliSecs() + 10000
				Menu_CreditY = 20
				Exit
			Case "#" ; zeilenende
				c.TCredit = New TCredit
				c\mesh = Txt_Text(txt$, MGui_Font, menu_cam)
				EntityOrder c\mesh ,-11
				PositionEntity c\mesh,0,Menu_CreditY,250
				MoveEntity c\mesh,-MeshWidth(c\mesh)/2,-MeshHeight(c\mesh)/2,0
				
				Menu_CreditY = Menu_CreditY - 10
				menu_creditTimer = MilliSecs() + 200
				Exit
			Case "*" ; spezialzeilenende
				c.TCredit = New TCredit
				c\mesh = Txt_Text(txt$, MGui_Font, menu_cam)
				EntityOrder c\mesh ,-11
				ScaleMesh c\mesh,1.2,1.2,1.2
				PositionEntity c\mesh,0,Menu_CreditY,250
				MoveEntity c\mesh,-MeshWidth(c\mesh)/2,-MeshHeight(c\mesh)/2,0
				EntityColor c\mesh,100,200,255
				
				
				Menu_CreditY = Menu_CreditY - 11
				menu_creditTimer = MilliSecs() + 200
				Exit
			Case "+" ; linkzeilenende
				c.TCredit = New TCredit
				c\mesh = Txt_Text(txt$, MGui_Font, menu_cam)
				EntityOrder c\mesh ,-11
				ScaleMesh c\mesh,.8,.8,.8
				PositionEntity c\mesh,0,Menu_CreditY,250
				MoveEntity c\mesh,-MeshWidth(c\mesh)/2,-MeshHeight(c\mesh)/2,0
				EntityColor c\mesh,255,180,100
				
				
				Menu_CreditY = Menu_CreditY - 8
				menu_creditTimer = MilliSecs() + 200
				Exit
			Case "<" ; creditende
				i = 0
				Menu_CreditY = 20
				menu_creditTimer = MilliSecs()+2000
				Exit
			Case "\"
				i = i + 1
				txt$ = txt$ + Mid(s,i,1)
			Default 
				txt$ = txt$ + ca
			End Select
		Next
		Menu_CPointer = i+1
	EndIf
	
	For c.TCredit = Each TCredit
		EntityAlpha c\mesh, c\alpha
		If Menu_CreditY = 20 And menu_creditTimer-MilliSecs()-200<1000
			c\alpha = (menu_creditTimer-MilliSecs()-200)/1000.0
			If (menu_creditTimer-MilliSecs()) < 210 Then FreeEntity c\mesh Delete c
		Else
			If c\alpha < 1 Then c\alpha = c\alpha + .05
		EndIf
	Next
End Function

Function Menu_CreditsClear()
	Menu_CPointer = 1
	Menu_CreditY = 20
	menu_creditTimer = 0
	For c.TCredit = Each TCredit
		FreeEntity c\mesh
		Delete c
	Next
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D