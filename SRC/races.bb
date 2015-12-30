Type class
	Field id
	Field team
	Field typ
	Field name$
	Field tickets#
	Field ship
	Field weapon[5]
	Field wammo[5]
End Type

Function Race_Load(path$,team)
	Util_CheckFile(path$)
	Stream = ReadFile(path$)
	Return Race_Parse(stream,team)
End Function

Function Race_Parse(stream,team)
	img = CreateImage(256,256)
	GrabImage img,0,0
	Repeat
		lin$ = Trim(ReadLine(stream))

		Select Util_Trim2(Lower(lin))
		Case "class{"
			Race_ParseClass(stream,team)
		Default
			If Util_GetParas(lin$)
				Select Lower(paras[0])
				Case "shield"
					teamid[team]\shield = LoadTexture(paras[1],1+2)
					TextureBlend teamid[team]\shield ,3
				End Select
			EndIf
		End Select
	Until lin = "}" Or Eof(stream)
	
	DrawBlock img,0,0
	FreeImage img
End Function


Function Race_ParseClass(stream,team)
	c.class	= New class
	c\team	= team
	c\typ	= 1
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		Select Lower(util_trim2(lin$))
		Case "weapon{"
			Race_ParseWeapon(stream,c)
		Default
			If Util_GetParas(lin$)
				Select Lower(paras[0])
				Case "id","num"
					c\id = paras[1]
				Case "name",main_lang+"name"
					c\name$	= paras[1]
				Case "tickets"
					c\tickets = paras[1]
				Case "file","ship"
					c\ship	= Shi_LoadShipClass(paras[1])
				Case "type","typ"
					c\typ	= paras[1]
				End Select
			EndIf
		End Select
	Until Right(lin,1)="}"
End Function

Function Race_ParseWeapon(stream,c.class)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "slot","group"
				slot= paras[1]
			Case "type"
				typ	= paras[1]
			Case "ammo"
				ammo = paras[1]
			End Select
		EndIf
	Until lin="}"
	c\weapon[slot] = typ
	c\wammo[slot]  = ammo
End Function

Function Race_GetClass(classi,team,typ=1)
	For c.class = Each class
		If c\team = team And c\typ = typ And c\id = classi
			Return c\ship
		EndIf
	Next
End Function

Function Race_GetShipsClass.class(shipclass,team,typ=1)
	For c.class = Each class
		If c\team = team And c\typ = typ
			If c\ship = shipclass Then Return c.class
		EndIf
	Next
End Function

Function Race_GetClassByID.class(id,team,typ)
	For c.class = Each class
		If c\team = team And c\typ = typ
			If c\id = id Then Return c.class
		EndIf
	Next
End Function


Function Race_Equip(sh.ship,team,classi,typ=1)
	;If typ = 2 Then Stop
	For c.class = Each class
		If c\team = team And c\typ = typ
			If c\id = classi Then Exit
		EndIf
	Next
	
	For i = 0 To 5
		If c\weapon[i] <> 0
			Shi_SetWeapon(sh,i,c\weapon[i],c\wammo[i])
		Else 
			Shi_SetWeapon(sh,i,0,0)
		EndIf
	Next
End Function

Function Race_Clear()
	Delete Each class
End Function