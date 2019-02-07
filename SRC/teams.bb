Type team
	Field id
	Field name$
	Field tickets#, stickets, ticketlost
	Field victories
	Field logo
	Field race$
	Field colr,colg,colb
	Field shipcount,flagcount
	Field shield
	Field maluscounter#
	Field commander.ship
End Type

Global teamid.team[4]

Function Team_JoinTeam(s.ship,team,dosend=1)
	s\team	= team
	
	s\colr	= teamid[team]\colr
	s\colg	= teamid[team]\colg
	s\colb	= teamid[team]\colb
	
	If s\nmesh Then EntityColor s\nmesh,s\colr,s\colg,s\colb
	
	If main_pl = s
		GUI_SetGadActivity(hud_but[0])
		GUI_SetGadActivity(hud_but[1])
		GUI_SetGadActivity(hud_but[team-1],2)
		
		ScaleTexture hud_paneltabtex,1-(2*(team=2)),1
	EndIf
	
	For c.class = Each class
		If c\team = team And c\Typ = 1
			num = num + 1
			shc.shipclass = classid[c\ship]
			If main_pl = s Then
				;GUI_GadTexture(hud_but[num+1],c\button)
				GUI_SetIcon(hud_but[num+1],shc\mini)
				GUI_SetCaption(hud_but[num+1],c\name,s\colr, s\colg, s\colb)
				GUI_SetHoverInfo(hud_but[num+1],shc\description)
			EndIf
		EndIf
	Next
	
	s\order = 0
	s\opiv = 0
	
	If s=main_pl And dosend=1 Then Shi_SendSpawnData(s)
	
	If (s\human = 1 Or s = main_pl) And Team_CountHumans(team) = 1 And (net=0 Or net_isserver=1) Then
		CC_Message("Command: SetCommander "+s\id)
	EndIf
End Function

Function Team_CountShips(team, without.ship)
	c = 0
	For s.ship = Each ship
		If s\team = team And s <> without And s\typ=1 Then
			sp.spawn = Fla_FindSpawnByID(s\selspawn)
			If sp <> Null Then 
				If sp\s <> s Then c = c + 1
			Else
				c = c + 1
			EndIf
		EndIf
	Next
	Return c
End Function

Function Team_CountHumans(team)
	c = 0
	For s.ship = Each ship
		If s\team = team And (s\human = 1 Or s = main_pl) Then
			c = c + 1
		EndIf
	Next
	Return c
End Function

Function Team_SetCommander(sid)
	If sid < 0 Then
		t.team = teamid[Abs(sid)]
		If t <> Null
			t\commander = Null
			HUD_PrintLog(t\name+" without commander.",255,255,0)
		EndIf
	Else
		s.ship = ships(sid)
		If s <> Null
			t.team = teamid[s\team]
			If t <> Null
				If t\commander = s Then Return
				t\commander = s
				If t\commander = main_pl Then
					hud_commandermode = 0
					HUD_PrintLog("You become commander of "+t\name,0,170,0)
				Else
					HUD_PrintLog(s\name+" becomes commander of "+t\name,0)
				EndIf
			EndIf
		EndIf
	EndIf
	
	For t.team = Each team
		If t\commander <> Null
			If t\commander\team <> t\id Then
				t\commander = Null
				HUD_PrintLog(t\name+" without commander.",255,255,0)
			EndIf
		EndIf
		If t\commander = Null And (net_isserver=1 Or net=0)
			If Team_CountHumans(t\id) > 0
				ts.ship = Null
				For s = Each ship
					If (s = main_pl Or s\human = 1) And s\team = t\id Then
						If ts = Null Then 
							ts = s
						ElseIf ts\points < s\points
							ts = s
						EndIf
					EndIf
				Next
				If ts <> Null Then CC_Message("Command: SetCommander "+ts\id)
			EndIf
		EndIf
	Next
End Function

Function Team_CreateTeam(id,name$,race,colr,colg,colb)
	t.team	= New team
	teamid[id] = t
	t\id	= id
	t\name$	= name
	t\race	= race
	t\colr	= colr
	t\colg	= colg
	t\colb	= colb
	Return Handle(t)
End Function

Function Team_Reset()
	For t.team = Each team
		t\tickets = t\stickets
	Next
End Function

Function Team_Clear()
	For t.team = Each team
		FreeTexture t\logo
		FreeTexture t\shield
		Delete t.team
	Next
End Function

Function Team_Parse(stream)
	t.team = New team
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "name",main_lang+"name"
					t\name$		= paras[1]
				Case "pic","logo"
					t\logo		= LoadTexture(paras[1],1+2)
				Case "num","id"
					t\id		= paras[1]
					teamid[t\id] = t
				Case "tickets"
					t\tickets	= paras[1]
					t\stickets	= paras[1]
				Case "race"
					t\race		= paras[1]
					Race_Load(datad+"RACES/"+t\race+".ini",t\id)
				Case "colr"
					t\colr		= paras[1]
				Case "colg"
					t\colg		= paras[1]
				Case "colb"
					t\colb		= paras[1]
			End Select
		EndIf
	Until lin="}"
	
	GUI_GadTexture(hud_but[t\id-1],t\logo)
	If t\id = 1
		EntityTexture hud_sfract1,t\logo
	ElseIf t\id = 2
		EntityTexture hud_sfract2,t\logo
	EndIf
	
	; in der spielerliste anzeigen
	mesh = CreateSprite(hud_list)
	EntityTexture mesh,t\logo
	ScaleSprite mesh,40,40
	PositionEntity mesh,-80+(t\id=2)*160,216 * main_hheight / main_hwidth,0
	EntityOrder mesh,-25
End Function
