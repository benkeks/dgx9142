Const ORDER_STOP	= 0
Const ORDER_ATTACK	= 1
Const ORDER_MOVETO	= 2
Const ORDER_FOLLOW	= 3

Function Com_ParseCommand$(txt$)
	comtxt$ = "Incorrect command"
	
	Util_GetParas(txt," ")
	Select paras[0]
	Case "SetCommander"
		Team_SetCommander(paras[1])
		comtxt$ = "-"
	Case "Attack"
		If Util_GetParas(paras[1]," ")
			ttxt0$ = paras[0]
			ttxt1$ = paras[1]
			While Util_GetParas(ttxt0,",")
				Com_DirectOrderS(ORDER_ATTACK, paras[0], ttxt1)
				ttxt0 = paras[1]
			Wend
			Com_DirectOrderS(ORDER_ATTACK, ttxt0, ttxt1)
		EndIf
		comtxt$ = "-"
	Case "MoveTo"
		If Util_GetParas(paras[1]," ")
			ttxt0$ = paras[0]
			ttxt1$ = paras[1]
			
			Util_GetParas(paras[1],",")
			x# = paras[0]
			Util_GetParas(paras[1],",")
			y# = paras[0]
			z# = paras[1]
			piv = CreatePivot()
			PositionEntity piv,x,y,z
			
			While Util_GetParas(ttxt0,",")
				Com_DirectOrderI(ORDER_MOVETO, paras[0], piv)
				ttxt0 = paras[1]
			Wend
			Com_DirectOrderI(ORDER_MOVETO, ttxt0, piv)
		EndIf
		comtxt$ = "-"
	Case "Stop"
		ttxt0 = paras[1]
		While Util_GetParas(ttxt0,",")
			Com_DirectOrderS(ORDER_STOP, paras[0], 0)
			ttxt0 = paras[1]
		Wend
		Com_DirectOrderS(ORDER_STOP, ttxt0, 0)
		comtxt$ = "-"
	End Select
	
	Return comtxt
End Function

Function Com_DirectOrderS(typ, addressee, target)
	If addressee < 0 Or addressee > 100 Or target < 0 Or target > 100 Then Return
	
	s1.ship = ships(addressee)
	If target=0 Then s2.ship = Null Else s2.ship = ships(target)
	If s1 = Null Or (s2 = Null And (typ=ORDER_ATTACK Or typ=ORDER_FOLLOW)) Then Return
	
	s1\order = typ
	s1\oship = s2
	If s1\opiv Then FreeEntity s1\opiv
	s1\opiv = 0
	
	If typ = ORDER_STOP
		For ki.kiplayer = Each kiplayer
			If ki\sh = s1 Then ki\globaction = 0
		Next
	EndIf
	
	;HUD_PrintLog(s1\name+" attacks "+s2\name,0,170,0)
	
	If s1 = main_pl
		
	EndIf
End Function

Function Com_DirectOrderI(typ, addressee, target)
	If addressee < 0 Or addressee > 100 Then Return
	
	s1.ship = ships(addressee)
	If s1 = Null Then Return
	
	s1\order = typ
	s1\oship = Null
	If s1\opiv Then FreeEntity s1\opiv
	s1\opiv = CopyEntity(target)
	
	;HUD_PrintLog(s1\name+" attacks "+s2\name,0,170,0)
	
	If s1 = main_pl
		
	EndIf
End Function