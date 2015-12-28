Type Conquest
	Field f.flag
	Field IfHoldBy
	Field Malus# ; Malus pro Sekunde
	Field ForT
End Type

Global Game_TeamBalance# = 1

Global Game_Pause = 0
Global Game_RestartTimer = 0
Global Game_Winner

Function Game_StartRound()
	Game_Pause = 0
	bloom_mbmode = 3
	Game_Winner = 0
	FX_ResetExplosions()
	Wreck_Clear()
	Wea_ClearShoots()
	Trail_Clear()
	Team_Reset()
	Fla_Reset()
	Shi_Reset()
	KI_Reset()
	Asteroids_Reset()
	HUD_SetMode(2)
	Music_SpecialTrack("game_start")
End Function 

Function Game_SetPause(pause=1)
	If net = 0 Then
		If Game_RestartTimer-2000 < MilliSecs() And hud_mode=4 Then pause = 1
		game_pause = pause
		hud_pause = pause
	EndIf
End Function

Function Game_Update()
	For t.team = Each team
		If t\shipcount = 0 And t\flagcount = 0 Then
			t\maluscounter = t\maluscounter + 20.0 * main_mscleft
		EndIf
		t\shipcount = 0
		t\flagcount = 0
		
		If t\maluscounter>=1000
			t\maluscounter = t\maluscounter - 1000
			t\tickets = t\tickets - 1
			t\ticketlost = 100
		EndIf
		
		If t\tickets <= 0 And (net=0 Or net_isserver=1)
			Game_Winner = 3-t\id
			Game_EndRound()
		EndIf
	Next
	
	For c.conquest = Each conquest
		If c\f\team = c\ifholdby And c\f\takeper > 90
			teamid[c\fort]\maluscounter = teamid[c\fort]\maluscounter + c\malus * main_mscleft
		EndIf
	Next
End Function

Function Game_EndRound()
	If net_isserver=1 Or net=0 Then teamid[Game_winner]\victories = teamid[Game_winner]\victories + 1
	If net_isserver Then Game_SendEndRound
	Game_RestartTimer = MilliSecs()+4000
	HUD_SetMode(4)
End Function 

Function Game_SendEndRound()
	Game_SendVictories()
	AddUDPByte(C_EndRound)
	AddUDPByte(game_winner)
	AddUDPShort(teamid[1]\tickets+12000)
	AddUDPShort(teamid[2]\tickets+12000)
	Net_urge = 1
End Function

Function Game_GetEndRound()
	game_winner = ReadUDPByte()
	teamid[1]\tickets = ReadUDPShort()-12000
	teamid[2]\tickets = ReadUDPShort()-12000
	Game_EndRound()
End Function

Function Game_SendVictories()
	AddUDPByte(C_Victories)
	AddUDPByte(teamid[1]\victories)
	AddUDPByte(teamid[2]\victories)
End Function

Function Game_GetVictories()
	teamid[1]\victories = ReadUDPByte()
	teamid[2]\victories = ReadUDPByte()
End Function

Function Game_Clear()
	Delete Each conquest
End Function