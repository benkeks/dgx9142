;DGX BattleSpace 9142
;von Mr.Keks - www.mrkeks.net

;Entwicklungsstart 25.März.2004

; ______ A B A N D O N E D _______

; It's been more than ten years since I wrote the three lines above.
; Actually, I planned to make a little game during the Easter break of 2004.
; I was 15 years old at that time.

; Since then, lots of stuff has happened, but the completion of DGX9142
; is not among them. Battlefied 1942, which inspired this game, has
; become some kind of a classic. DirectX 7, which is used for the graphics,
; has long reached the end of its life cycle. I and all the other people
; who have contributed to DGX9142 do quite different things by now.
; For my part, I am more into theoretical computer science, print design
; and political stuff today.

; Over the last six years, DGX9142 has hardly been developed any
; further. The last public alpha was released in March 2008.
; So I and Steff decided to abandon the project. We don't want it to
; rot on our hard drives. Thus we release it. This final release is
; in a way the most unfinished version of DGX9142 that has ever been
; published: Some textures are missing, the net code most likely
; won't work properly, there is no real balancing etc. etc.

; We hope you enjoy the game anyway. :)

;  -- 25 December 2015, Benjamin Bisping (aka Mr.Keks)


; ...We also include the source with the game. It's not really open source
; as the source is not "open" in the sense of: Easy to maintain by others.
; (No documentation, poor structure, some lazy hacks, redundancies,
; out-dated programing language) Still: Feel free to use the source
; in any way you like, be it in other projects or in a more polished
; version of DGX. If you continue DGX and publish your work, please include
; the original credits with it. The third party parts within DGX are marked
; as such by comments. The artwork is by the credited people or from free
; ware archives. You may use it in derived versions of DGX, but not in any
; new context - at least we can't grant you this permission as it is up
; to the very authors.


AppTitle "DGX 9142"

;-------------------

Const datad$ = "DATA/"
Const gfxd$ = "GFX/"

Const main_version$ = "abandoned alpha 0.1.0"
Const main_version_id$ = "010"
Const main_mrkeks = 0

Global Main_QuitGame=0
Global Main_Map$ = ""
Global Main_Dedicate = 0
Global Main_MapChange$ = ""
Global Main_Restart=1
Global Main_Debug = 0
Global Main_CriticalFPS# = 0

Dim ships.ship(256)

SeedRnd MilliSecs()

;-------------------
;main.bb				;Main_

Include "SRC/consts.bb"		;

Include "SRC/util.bb"		;Util_

Include "SRC/initgfx.bb"	;Main_
Include "SRC/load.bb"		;
Include "SRC/language.bb"	;lang_

Include "SRC/environ.bb"	;Env_
Include "SRC/gates.bb"
Include "SRC/music.bb"		;Music_

Include "SRC/teams.bb"		;Team_
Include "SRC/races.bb"		;Race_

Include "SRC/weapons.bb"	;Wea_
Include "SRC/ships.bb"		;Shi_
Include "SRC/ki.bb"			;KI_
Include "SRC/botnames.bb"	;KI_

Include "SRC/specialfx.bb"	;FX_
Include "SRC/dust.bb"		;Dust_
Include "SRC/cloak.bb"		;Cloak_
Include "SRC/trails.bb"		;trail_
Include "SRC/wrecks.bb"		;wreck_
Include "SRC/asteroids.bb"	;asteroids_
Include "SRC/turrets.bb"	;turr_
Include "SRC/hud.bb"		;HUD_
Include "SRC/gui.bb"		;GUI_
Include "SRC/hudlog.bb"		;HUD_
Include "SRC/hudplayers.bb"	;HUD_
Include "SRC/hudcommander.bb"
Include "SRC/hudhoverinfo.bb"
Include "SRC/controlcam.bb"	;CC_
Include "SRC/commander.bb"	;Com_
Include "SRC/minimap.bb"	;HUD_
Include "SRC/input.bb"		;Inp_
Include "SRC/3Dmouse.bb"	;Mouse_

Include "SRC/game.bb"		;game_
Include "SRC/events.bb"

Include "SRC/udp_includefile_v1.bb"
Include "SRC/network.bb"

Include "SRC/key_routine.bb";Key_

; Menü

Include "SRC/menu.bb"		;Menu_
Include "SRC/menugui.bb"

;-- AE-Module						
; (Code derived from "Aliens Exist." - a shooter programed by INpac and Mr.Keks)

Const aed$ = "SRC/AEMODULE/"

Include "SRC/AEMODULE/map.bb"
Include "SRC/AEMODULE/aemlib.bb"
Include "SRC/AEMODULE/afxlib.bb"
Include "SRC/AEMODULE/parser.bb"

Include "SRC/AEMODULE/CubicLib.bb"
Include "SRC/AEMODULE/OceanLib.bb"
Include "SRC/AEMODULE/Dot3Lib.bb"
Include "SRC/AEMODULE/audio.bb"
Include "SRC/AEMODULE/shader.bb"
Include "SRC/AEMODULE/textlib.bb"

Include "SRC/AEMODULE/object.bb"

Include "SRC/AEMODULE/flags.bb"

Include "SRC/AEMODULE/bloomfilter.bb"


Main_EvaluateCommandLine()

;-------------------

While Main_Restart
	Main_InitGfx()
	Repeat
		Main_Load()
		
		Util_InitTimer()
		Main_Restart = 0
	Until Menu_Start()
Wend

End

;-------------------
Function Main_Play(map$,players,mode=1,server$="")
	Main_QuitGame = 0
	Main_Dedicate = 0
	Main_MapChange = ""
	main_pl.ship = Null
	Main_Map = map
	gkey = ""
	
	Select mode
	Case 1
		net = 0
		net_isserver = 0
	Case 2
		net = 1
		net_isserver = 1
		net_rate = net_srate
	Case 3
		net = 1
		net_isserver = 0
		net_rate = net_crate
	End Select
	net_name = main_name
	
	Util_ClearCheckSums()
	
	CC_Init()
	FX_Init()
	Wea_LoadWeapons()
	Shi_Init()
	Env_Init()
	Wreck_Init()
	HUD_SetColor(50,150,240)
	HUD_Init()
	Mouse_init(hud_cam_piv)
	Cloak_Init()
	Trail_Init()
	Gate_Init()
	Fla_SpawnCount = 0
	
	If net Then
		If net_isserver Then
			Net_OpenServer(server$)
		Else
			Net_ConnectToServer(server$,main_name)
		EndIf
	EndIf
	
	If ki_tpiv = 0 Then ki_tpiv = CreatePivot()
	
	If Main_QuitGame = 0
		;If main_shadows Then InitShadows(cc_Cam)
		
		Map_Load("Maps/"+Main_Map$)
		
		Gate_GetReady()
		HUD_OrderFlags()
		If net=1 And net_isserver = 0 Then
			Net_Compare()
			Delay 500
			Net_AskForShip()
			Net_CSynchronize()
		EndIf
		
		If net_isserver = 1 Or net = 0
			main_pl.ship = Shi_CreateShip(1,0,4,1,main_name,1,0, 1,0,0,  net_isserver)
			HUD_SetPlayer()
			Team_JoinTeam(main_pl,1)
			
			KI_ReadBotnames()
			
			For i = 1 To players/8
				Shi_CreateShip(0,0,0,	1,KI_BotName(1),1,1)
				Shi_CreateShip(0,0,0,	2,KI_BotName(1),1,1)
				Shi_CreateShip(0,0,0,	3,KI_BotName(1),1,1)
				Shi_CreateShip(0,0,0,	4,KI_BotName(1),1,1)
				Shi_CreateShip(0,0,0,	1,KI_BotName(2),2,1)
				Shi_CreateShip(0,0,0,	2,KI_BotName(2),2,1)
				Shi_CreateShip(0,0,0,	3,KI_BotName(2),2,1)
				Shi_CreateShip(0,0,0,	4,KI_BotName(2),2,1)
			Next
		EndIf
		
		Music_SpecialTrack("game_start")
		
		If Main_QuitGame = 0
			HUD_SetMode(2)
			
			Util_InitTimer()
			
			time5 = MilliSecs()
			Game_Pause = 0
				
			While Main_QuitGame=0
				If net
					If net_lastmsc + 1000 < MilliSecs()
						net_data = net_datasum
						net_datasum = 0
						net_lastmsc = MilliSecs()
					EndIf
					If UDP_DataBankPosition[UDP_DataStream] > 128 * (net_isserver=1 And net_urge=0 And net_update=0) Then
						AddUDPByte(C_End) : SendUDPData()
						net_urge = 0
					EndIf
					net_update = 0
					If net_isserver Then
						If MilliSecs()-net_lastcommunication > 60000 And net_listid <> 0 Then Net_LifeSign()
						UDP_HandleServer()
					Else
						UDP_HandleClient()
					EndIf
					If MilliSecs()-net_updatetimer > net_rate Then
						net_updatetimer = MilliSecs()
						net_update = 1
						AddUDPByte(C_Update)
					EndIf
				EndIf
				
				;Cloak_Update()
				
				time1 = MilliSecs()
				
				If Main_Dedicate = 0 Then Wat_UpdateAllWater() : Dust_Update()
				
				
				;HUD_Hide
				If main_bloom>0 Then RenderGlow Else bloom_effect2 = (bloom_effect2*.9 + bloom_mb*.1)
				;HUD_Show
				If main_shadows Then
					;render(1)
				Else
					If Main_Dedicate = 0 Then 
						RenderWorld()
					Else
						Cls
					EndIf
				EndIf
				renderedtris = TrisRendered()
				time2 = MilliSecs()
				If Main_Debug Then
					Color 255,255,255
					Text 0,45,"Update: " + (time1-time3)
					Text 0,60,"Rendering: " + (time2-time1)
				EndIf
				Util_Timer()
				
				If Game_Pause = 0 Or net = 1
				
					Shi_UpdateShips()
					Tur_Update()
					Wreck_Update()
					Obj_Update()
					Gate_Update()
					
					time2 = MilliSecs()
					If Main_Debug Then Text 0,90,"Ship etc" + (time2-time1)
					
					UpdateWorld()
					
					time1 = MilliSecs()
					If Main_Debug Then Text 0,105,"Collisions " + (time1-time2)
					
					Wea_UpdateShoots()
					Wea_UpdateWaves()
					
					Trail_Update()
					
					time2 = MilliSecs()
					If Main_Debug Then Text 0,120,"Weapons & Trails " + (time2-time1)
					
					KI_Update()
						
					time1 = MilliSecs()
					If Main_Debug Then Text 0,135,"AI " + (time1-time2)
					
					If Main_Dedicate = 0 Then Sha_Update()
					Fla_Update()
					
					If Game_Winner = 0 Then Game_Update()
				Else 
					FX_UpdatePause()
				EndIf
				
				If hud_mode = 0 And Game_Pause=0 Then
					CC_Update()
				Else		
					CC_CamUpdate()
				EndIf				
				
				If Game_Pause = 0 Or net = 1
					FX_Update()
				EndIf
				time1 = MilliSecs()
				If Main_Debug Then Text 0,150,"Cam & FX" + (time1-time2)
				time2 = MilliSecs()
				
				HUD_Update() 
				HUD_UpdateLog()
				HUD_UpdateInput()
				Hud_UpdateHoverInfo()
				
				Aud_Update()
				
				time1 = MilliSecs()
				If Main_Debug Then Text 0,75,"HUD: " + (time1-time2)
				
				;Prs_Update();
				
				Hud_UpdateMinimap()
				Env_Update()
				Music_Update()
				Inp_Mouse()
				Mouse_Update()
				;Event_HandleEvents()
				
				renderedtris = renderedtris + TrisRendered()
				
				time2 = MilliSecs()
				If Main_Debug Then
					Text 0,150,"Misc" + (time2-time1)
					Text 0,0,1000/(MilliSecs()-time3)
					Text 0,15,renderedtris + "    " + AvailVidMem()/1024/1024+" / "+TotalVidMem()/1024/1024 + " MB"
					Text 0,30,main_gspe
					Text 100,50,"Total time: " + (MilliSecs()-time3)
					totms = totms + (MilliSecs()-time3)
					totframes = totframes + 1
					Text 100,70,"Average FPS: "+ Int(1000/Float((MilliSecs()-time5)/Float(totframes)))
				EndIf
				
				averageTotMS# = averageTotMS# * .95 + Float(MilliSecs()-time3)*.05
				
				; details runterschrauben bei kritischen fps
				If averageTotMS > 28 Then
					Main_CriticalFPS = Float((MilliSecs()-time3)-28)*0.03 + 1
				Else
					Main_CriticalFPS = 0
				EndIf 
				
				Flip main_vsync
				
				time3 = MilliSecs()
				
				If KeyHit(cc_screenshot)
					Util_Screenshot("SCREENS/Screen")
				EndIf
				If KeyHit(cc_pause) And hud_input\active = 0 And hud_mode <> 4
					Game_SetPause(1-Game_Pause)
				EndIf
				
				If hud_input\active = 0 Then
					If KeyHit(1) Then
						If hud_reallyquit <> 1 Then
							hud_reallyquit = 1
							PlaySound hud_warning
						EndIf
						gkey = ""
					EndIf
				EndIf
			Wend
		EndIf
		
		FlushKeys
		
		If net_isserver Then
			Net_CloseServer()
			If net_listid<>0 Then Net_RemoveFromServerList()
		ElseIf net
			Net_LeaveServer(1)
		EndIf
		net_listid = 0
	EndIf
	
	If net Then Net_Clear()
	
	Prs_Clear()
	Aud_Clear()
	Gui_Clear()
	Glow_Clear()
	Sha_Clear()
	Gate_Clear()
	Trail_Clear(1)
	Obj_Clear()
	Hud_ClearCommander()
	Hud_ClearHoverInfo()
	HUD_ClearPlayers()
	HUD_ClearInput()
	HUD_ClearLog()
	HUD_ClearMiniMap()
	HUD_Clear()
	FX_Clear()
	Dust_Clear()
	Wreck_Clear(1)
	Asteroids_Clear()
	Wea_Clear()
	CC_Clear()
	KI_Clear()
	KI_ClearBotnames()
	Shi_Clear()
	Shi_ClearClasses()
	Team_Clear()
	Race_Clear()
	Env_Clear()
	Fla_Clear()
	Map_Clear()
	Game_Clear()
	
End Function


Function Main_EvaluateCommandLine()
	lin$ = CommandLine()
	If Instr(lin,"-debug") <> 0 Then Main_Debug = 1
	If Instr(lin,"-windowed") <> 0 Then main_mode = 222
	If Instr(lin,"-quickstart") <> 0 Then
		menu_gamemode = 4
		menu_startgame = 1
		firststart = 0
	EndIf
	
	If Instr(CurrentDate(), "01 Apr") <> 0 Then
		menu_failure = "Die Y'thear und die Evianer haben Frieden geschlossen. Es gibt nichts mehr zu kämpfen. Beende das Spiel und such dir ein anderes Schlachtfeld!"
	ElseIf Instr(CurrentDate(), "01 May") <> 0 
		menu_failure = "Für den ersten Mai empfehlen wir: Unerwarteter Widerstand!"
	EndIf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D
