Const net_listserver_addr$ = "dgx.mrkeks.net"
Const net_listserver_port = 80
Const net_listserver_dir$ = "/"+main_version_id+"/"

Global net_port=9142

Global net, net_isserver=1

Global net_id, net_name$ = "player"
Global net_ready

Global net_update=0
Global net_updatetimer=0
Global net_srate=200
Global net_crate=50
Global net_rate
Global net_stime, net_lastupdate
Global net_urge
Global net_listid, net_lastcommunication

Global net_data, net_datasum, net_lastmsc

Function Net_OpenServer(Name$)
	net_ID = CreateUDPServer(net_port,name$)
	HUD_PrintLog( "Server"+net_id+" >"+name$+ "< (Port:"+net_port+") created!",50,150)
End Function

Function Net_ConnectToServer(IP$,Name$)
	net_ID = JoinUDPGame(IP$,net_Port,Name$, main_Port)
	HUD_PrintLog( "Connected to server >"+UDP_Server_Name$ + "< ("+ DottedIP(UDP_Server_IP) +")",50,150)
End Function

Function Net_Compare()
	t = 0
	AddUDPByte(S_Compare)
	For c.TCheckSum = Each TCheckSum
		AddUDPString(c\name)
		AddUDPInteger(c\sum)
		If c <> Last TCheckSum Then AddUDPByte(2) Else AddUDPByte(1)
	Next
	SendUDPData()
End Function

Function Net_AskForShip()
	AddUDPByte(S_AskForShip)
	SendUDPData()
End Function

Function Net_CSynchronize()
	net_ready = 0 
	timer = MilliSecs()+3000
	Repeat
		UDP_HandleClient()
		If MilliSecs()>timer Then
			If main_pl = Null Then Net_AskForShip()
			timer = MilliSecs()+3000
		EndIf
	Until (net_ready=1 And main_pl <> Null) Or main_quitgame
End Function

Function Net_SSynchronize(Id)
	UDP_DataStream = 1
	
	DebugLog "Synchronizing Client "+id
	
	Game_SendVictories()
	
	For f.flag = Each flag
		Fla_SendData(f.flag)
	Next
	
	AddUDPByte(c_end)
	SendUDPData(id)
	For s.ship = Each ship
		Shi_SendCreateShip(s)
		Shi_SendScore(s)
		If s = main_pl Or s\human = 1 Then
			AddUDPByte(C_PlayerShip)
			t=0
			For u.UDP_User = Each UDP_User
				If u\ship=s Then
					AddUDPByte(u\id)
					t=1
					Exit
				EndIf
			Next
			If t = 0 Then AddUDPByte(255)
			AddUDPByte(s\id)
		EndIf
	Next
	
	For w.wreck = Each wreck
		If w\typ = WRECK_ASTEROID Then
			Wreck_SendWreck(w)
		EndIf
	Next
	
	If teamid[1]\commander <> Null Then
		AddUDPByte(C_Message)
		AddUDPByte(0) ; an alle
		AddUDPString("Command: SetCommander "+teamid[1]\commander\id)
	Else
		AddUDPByte(C_Message)
		AddUDPByte(0) ; an alle
		AddUDPString("Command: SetCommander -1")
	EndIf
	If teamid[2]\commander <> Null Then
		AddUDPByte(C_Message)
		AddUDPByte(0) ; an alle
		AddUDPString("Command: SetCommander "+teamid[2]\commander\id)
	Else
		AddUDPByte(C_Message)
		AddUDPByte(0) ; an alle
		AddUDPString("Command: SetCommander -2")
	EndIf
	
	AddUDPByte(c_ready)
	UDP_Users(id)\ready = 1
	AddUDPByte(c_end)
	SendUDPData(id)
	UDP_DataStream = 0
End Function

; ### NETZWERK-REAKTIONS-ROUTINE ###################

Function UDP_HandleGameStatus(UDP_MessageID,UDP_UserID=0,UDP_UserName$="",UDP_UserIP=0,UDP_UserPort=0)
	If net_isserver
		UDP_SHandleGameStatus(UDP_MessageID,UDP_UserID,UDP_UserName$,UDP_UserIP,UDP_UserPort)
	Else
		UDP_CHandleGameStatus(UDP_MessageID,UDP_UserID,UDP_UserName$,UDP_UserIP,UDP_UserPort)
	EndIf
End Function

Function UDP_SHandleGameStatus(UDP_MessageID,UDP_UserID=0,UDP_UserName$="",UDP_UserIP=0,UDP_UserPort=0)
	DebugLog UDP_MessageID
	
	Select UDP_MessageID
	Case UDP_NewUser
		HUD_PrintLog (" > Anmeldung: " + Chr$(34) + UDP_UserName$ + Chr$(34) + ", IP: " + DottedIP(UDP_UserIP) + ", Port: " + UDP_UserPort + ", zugewiesene ID: " + UDP_UserID),10,170,10
		CC_Message("> "+UDP_UserName+" joined",1)
	Case UDP_IncomingData
		Net_Server(UDP_UserID,UDP_UserName$,UDP_UserIP,UDP_UserPort)
	Case UDP_UserLeft
		HUD_PrintLog (" > User " + Chr$(34) + UDP_UserName$ + Chr$(34) + " verlässt das Spiel"),10,170,10
		CC_Message("> "+UDP_UserName+" left",1)
	End Select
	
	Select UDP_MessageID
	Case UDP_UnknownError
		menu_failure$ = "Ein unbekannter Fehler ist aufgetreten!"
		Net_CloseServer()
	Case UDP_NetworkError
		menu_failure = "Netzwerkfehler!"
		Net_CloseServer()
	Case UDP_TimeOut
		HUD_PrintLog " > User " + Chr$(34) + UDP_UserName$ + Chr$(34) + " wurde gelöscht: TimeOut (" + UDP_TimeOutLength + ")",255,255,0
		CC_Message("> "+UDP_UserName+" left: Time Out!",1)
	Case UDP_LoginError
		HUD_PrintLog " > Login von User " + Chr$(34) + UDP_UserName$ + Chr$(34) + " fehlgeschlagen!",255,255,0
	End Select
	UDP_OccuredError = 0
End Function

Function UDP_CHandleGameStatus(UDP_MessageID,UDP_UserID=0,UDP_UserName$="",UDP_UserIP=0,UDP_UserPort=0)
	DebugLog UDP_MessageID
	
	Select UDP_MessageID
	Case UDP_IncomingData
		Net_Client()
	Case UDP_UnknownError
		menu_failure = "Ein unbekannter Fehler ist aufgetreten!"
		main_quitgame = 1
	Case UDP_NetworkError
		menu_failure = "Netzwerkfehler!"
		main_quitgame = 1
	Case UDP_NameAlreadyExists
		menu_failure = "Name existiert bereits!"
		main_quitgame = 1
	Case UDP_LoginError
		menu_failure = "Verbindungsfeler!"
		main_quitgame = 1
	Case UDP_TimeOut
		menu_failure = "TimeOut! (" + UDP_TimeOutLength + " Sekunden)"
		main_quitgame = 1
	Case UDP_ServerFull
		menu_failure = "Server ist voll!"
		main_quitgame = 1
	Case UDP_Kick
		reason$ = ReadString(UDP_Stream)
		If reason = "Mapchange"
			main_mapchange = "mapchange"
		Else
			menu_failure = "Du wurdest gekickt! "+Reason$
		EndIf
		main_quitgame = 1
	End Select
End Function 


Function Net_CloseServer()
	For UDP_TempUser.UDP_User = Each UDP_User
		If main_mapchange = "" Then 
			UDP_KickUser(UDP_TempUser\id,"Server closed")
		Else
			UDP_KickUser(UDP_TempUser\id,"Mapchange")
		EndIf
	Next
	EraseUDPUser
	QuitUDPGame
	Main_QuitGame=1
End Function

Function Net_LeaveServer(send=0)
	If send = 1
		If UDP_DataBankPosition[UDP_DataStream] > 0 Then
			AddUDPByte(C_End) : SendUDPData()
		EndIf
		UDP_UserLeave()
	EndIf
	QuitUDPGame
	Main_QuitGame=1
End Function

Const C_End = 0

Const C_NameChange		= 7
Const C_Message			= 8
Const C_PlayerShip		= 9

Const C_CreateShip		= 10
Const C_FlagData		= 11

Const C_WreckData		= 15
Const C_WreckDestruction= 16

Const C_PositionShip	= 20
Const C_ShipDeath		= 21
Const C_DeleteShip		= 22
Const C_SpawnData		= 23
Const C_Score			= 24
Const C_Ammo			= 25

Const C_Fire			= 30
Const C_Hit				= 31
Const C_AimAt			= 32
Const C_TurFire			= 33
Const C_ShotTarget		= 34

Const C_EndRound		= 40
Const C_Victories		= 41

Const C_Ready = 100

Const C_Update = 255

Function Net_Client()
	Repeat
		typ = ReadUDPByte()
		;DebugLog "Data: "+typ
		Select typ
		Case C_NameChange
			CC_GetNameChange()
		Case C_Message
			CC_GetMessage()
			
		Case C_FlagData
			Fla_GetData()
			
		Case C_WreckData
			Wreck_GetWreck()
		Case C_WreckDestruction
			Wreck_GetWreckDestruction()
			
		Case C_PlayerShip
			player = ReadUDPByte()
			ship = ReadUDPByte()
			If player = UDP_ID Then 
				If main_pl = Null Then
					Main_pl = ships(ship)
					Team_JoinTeam(main_pl,1+(Team_CountShips(1,main_pl)>Team_CountShips(2,main_pl)))
					HUD_SetPlayer()
				EndIf
			Else
				Shi_SetHuman(ships(ship))
			EndIf
		Case C_CreateShip
			Shi_GetCreateShip()
		Case C_PositionShip
			Shi_GetPosition()
		Case C_ShipDeath
			Shi_GetDeath()
		Case C_DeleteShip
			Shi_GetDeleteShip()
		Case C_SpawnData
			Shi_GetSpawnData()
		Case C_Score
			Shi_GetScore()
		Case C_Ammo
			Shi_GetAmmo()
			
		Case C_Fire
			Shi_GetFire()
		Case C_Hit
			Wea_GetHit()
		Case C_AimAt
			Tur_GetAimAt()
		Case C_TurFire
			Tur_GetFire()
		Case C_ShotTarget
			Wea_GetTarget()
			
		Case C_EndRound
			Game_GetEndRound()
		Case C_Victories
			Game_GetVictories()
			
		Case C_Ready
			net_ready = 1
		Case C_End
			Exit
			net_lastupdate = MilliSecs()
		Case C_Update
			net_stime = MilliSecs()-net_lastupdate
			net_lastupdate = MilliSecs()
		End Select
	Until Eof(UDP_Stream)
	;net_update = 1
End Function

Const S_Compare = 1
Const S_AskForShip = 10

Const S_NameChange = 18
Const S_Message = 19
Const S_SpawnData = 20
Const S_Position = 21
Const S_Fire = 22

Function Net_Server(UDP_UserID,UDP_UserName$,UDP_UserIP,UDP_UserPort)
	If UDP_Users(UDP_UserID) = Null
		Repeat
			ReadUDPByte()
		Until Eof(UDP_Stream)
	Else
		Repeat
			typ = ReadUDPByte()
			Select typ
			Case S_Compare
				DebugLog "Comparing "+UDP_UserName
				kicked = 0
				Repeat
					file$ = ReadUDPString()
					sum	= ReadUDPInteger()
					For c.TCheckSum = Each TCheckSum
						If file$ = c\name$
							If sum <> c\sum Then
								UDP_KickUser(UDP_UserID,"Falsche Spielversion!")
								CC_Message("> "+UDP_UserName+" kicked (wrong version / cheat attempt)",1)
								kicked = 1
							EndIf
							Exit
						ElseIf FileType(file$)=0 
							kicked = 1
							UDP_KickUser(UDP_UserID,"Falsche Spielversion!")
							CC_Message("> "+UDP_UserName+" kicked (wrong version / cheat attempt) "+file$,1)
							Exit
						EndIf
					Next
				Until ReadUDPByte() = 1 Or kicked = 1
				If kicked = 0 Then
					Net_SSynchronize(UDP_UserID)
				Else
					Repeat
						ReadUDPByte()
					Until Eof(UDP_Stream)
				EndIf
			Case S_AskForShip
				DebugLog UDP_UserName + " asked for a ship"
				team = 1+(Team_CountShips(1,Null)>Team_CountShips(2,Null))
				DebugLog "Select team: "+team
				If UDP_Users(UDP_UserID)\ship = Null Then
					s.ship = Object.ship(Shi_CreateShip(1,0,4,1,UDP_UserName,team,0, 1,0,0))
					AddUDPByte(C_PlayerShip)
					AddUDPByte(UDP_UserID)
					AddUDPByte(s\id)
					UDP_Users(UDP_UserID)\ship = s
					Shi_SetHuman(s)
				EndIf
			Case S_NameChange
				u.UDP_User = UDP_Users(UDP_UserID)
				CC_GetCNameChange(u\ship)
			Case S_Message
				CC_GetCMessage()
			Case S_SpawnData
				Shi_GetSpawnData()
			Case S_Position
				Shi_GetCPosition()
			Case S_Fire
				Shi_GetCFire()
			Case C_Update
				net_stime = MilliSecs()-net_lastupdate
				net_lastupdate = MilliSecs()
			Case C_End
				Exit
			End Select
		Until Eof(UDP_Stream)
	EndIf
	;net_update = 1
End Function

Function Net_Clear()
	net_urge = 0
	net_stime = 0
	net_lastupdate = 0
	net_update = 0
	net_updatetimer = 0
	net_ready = 0
	
	net = 0
	net_isserver = 0
	
	For p.packet = Each packet
		FreeBank p\bank
		Delete p
	Next
	
	QuitUDPGame()
	
	Delete Each udp_user
	Delete Each missingpacket
End Function

Function Net_AddToServerList(name$,port,map$)
        stream = OpenTCPStream(net_listserver_addr, net_listserver_port)
	If stream <> 0
		name$ = Util_MakeURL(name$)
		map$ = Util_MakeURL(map$)
		WriteLine stream,"GET http://"+net_listserver_addr+net_listserver_dir+"addserver.php?sname="+name$+"&sport="+port+"&smap="+map$
		While Not Eof(stream)
			txt$ = ReadLine(stream)
		Wend
		Util_GetParas(txt,",")
		id = paras[0]
		time$ = paras[1]
		CloseTCPStream stream
		stream = OpenTCPStream(net_listserver_addr, net_listserver_port)
		If stream <> 0
			WriteLine stream,"GET http://"+net_listserver_addr+net_listserver_dir+"addserver.php?validate="+id
			While Not Eof(stream)
				txt$ = ReadLine(stream)
			Wend
			If txt$ = "ok" Then DebugLog "Eintrag in DGX-Serverliste erfolgreich!"
			
			CloseTCPStream stream
		EndIf
		net_listid = id
		net_lastcommunication = MilliSecs()
	EndIf
End Function

Function Net_LifeSign()
	stream = OpenTCPStream(net_listserver_addr, net_listserver_port)
	If stream <> 0
		WriteLine stream,"GET http://"+net_listserver_addr+net_listserver_dir+"addserver.php?validate="+net_listid
		CloseTCPStream stream
	EndIf
	net_lastcommunication = MilliSecs()
End Function

Function Net_RemoveFromServerList()
        stream = OpenTCPStream(net_listserver_addr, net_listserver_port)
	If stream <> 0
		WriteLine stream,"GET http://"+net_listserver_addr+net_listserver_dir+"addserver.php?remove="+net_listid
		CloseTCPStream stream
	EndIf
End Function

Function Net_GetServerList()
	If menu_udpstream Then CloseUDPStream menu_udpstream
	If main_port Then menu_udpstream = CreateUDPStream(main_port) Else menu_udpstream = CreateUDPStream()
	If menu_udpstream = 0 Then RuntimeError "Unable to create a UDP stream"
	
        stream = OpenTCPStream(net_listserver_addr, net_listserver_port)
	If stream <> 0
		WriteLine stream,"GET http://"+net_listserver_addr+net_listserver_dir+"serverlist.php"
		Return stream
	EndIf
End Function