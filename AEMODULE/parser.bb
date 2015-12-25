Global Prs_Pointer 	= -1
Global Prs_LToken$	= 0

Global Prs_VarID = 0

Global Prs_CurrEvent.event

Type prs_brackets
	Field file
	Field startpos
	Field endpos
	Field depth
End Type

Type prs_var
	Field file
	Field name$
	Field id
End Type

Const PRS_TIMER = 1
Const PRS_EVENT = 2

Type prs_void
	Field typ
	Field file
	Field name$
	Field active
	
	Field condition$, csource$
	Field updaterate
	Field lastupdate
	
	Field delaying
	Field start
	Field ende
End Type

Type prs_file
	Field file
End Type

Dim vars$(1000)

Function Prs_FindVoid.prs_void(name$,file)
	For v.prs_void = Each prs_void
		If v\name = name And v\file = file
			Return v.prs_void
		EndIf
	Next
End Function

Function Prs_Clear()
	For f.prs_file = Each prs_file
		Prs_CloseScript(f\file)
		Delete f
	Next
End Function

Function Prs_CloseScript(file)
	For v.prs_void = Each prs_void
		If v\file = file
			Delete v.prs_void
		EndIf
	Next
	For b.prs_brackets = Each prs_brackets
		If b\file = file
			Delete b.prs_brackets
		EndIf
	Next
	FreeBank file
End Function

Function Prs_Update()
	For f.prs_file = Each prs_file
		Prs_UpdateFile(f\file)
	Next
End Function

Function Prs_UpdateFile(file)
	ms = MilliSecs()
	For v.prs_void = Each prs_void
		If v\file = file
			If (util_lastmsc > v\lastupdate + v\updaterate Or v\delaying <> 0) And v\active
				Color 255,0,0
				
				If v\delaying = 0
					Prs_Interpret(file,v\start,v\ende,v)
				Else
					start		= v\delaying
					v\delaying	= 0
					Prs_Interpret(file,start,v\ende,v)
				EndIf
				
				If v = Null Then Exit
				If v\delaying = 0 Then v\lastupdate = util_lastmsc
				If v\typ = PRS_EVENT Then v\updaterate = 999999999
			ElseIf v\active = 0
				Color 128,128,128
			Else
				Color 255,255,255
			EndIf
		EndIf
		If developmode Then Text 100,i,v\name+"  (start: "+v\start+"/ ende: "+v\ende+")"
		i = i + 20
	Next
	If developmode Then Text 100,100,"scripttime: "+(MilliSecs()-ms)
End Function

Function Prs_InterpretEvent(v.prs_void, e.event)
	Prs_CurrEvent = e
	Prs_Interpret(v\file,v\start,v\ende,v)
End Function

Function Prs_Interpret(file,start,ende,v.prs_void)
	prs_pointer = start
	Repeat
		token$ = Prs_GetToken(file,Prs_Pointer)
		If Prs_Pointer >= ende
			Exit
		Else
			DebugLog token
			
			Prs_InterpretCommand$(file,token,v)
			If v = Null Then Exit
			If v\delaying Then Exit
		EndIf
	Forever
End Function

Function Prs_InterpretCommand$(file,token$,v.prs_void)
	Local retur$
	
	eot$ = Right(token,1)
	
	Select Left(token,Len(token)-1)
	Case "var"; var variable$ = value$ ; define / set variable
		var$ = Prs_GetToken(file,Prs_Pointer)
		
		typ$ = Right(prs_ltoken,1)
		If typ = "$"
			typid = 0
		Else
			RuntimeError "Expecting Variable ($)"
		EndIf
		
		id = Left(var,Len(var)-1)
		
		
		If Prs_GetToken(file,Prs_Pointer) <> "=" Then RuntimeError "Expecting ="
		
		para$ = Prs_GetPara(file,Prs_Pointer)
		
		
		If typid=0 Then Vars(id) = para
		
		DebugLog "Set Var"+id+" To "+ para
	Case "if" ; if ( true ) { ... }
		Prs_Pointer = Util_FindNext(file,prs_pointer-1,Asc("("))
		
		condition	= Prs_GetPara$(file,prs_pointer)
		
		start		= Util_FindNext(file,prs_pointer,Asc("{"))
		
		If condition
			Prs_Pointer = start
		Else
			For b.prs_brackets = Each prs_brackets
				If b\startpos = start And b\file = file
					Prs_Pointer = b\endpos
					Exit
				EndIf
			Next
		EndIf
		DebugLog condition
	Case "print" ; print Text$
		para$ = Prs_GetPara(file,Prs_Pointer)
		HUD_PrintLog(para)
		DebugLog "Print "+para
	Case "print_lang"
		language$ = Prs_GetPara(file,Prs_Pointer)
		para$ = Prs_GetPara(file,Prs_Pointer)
		If language = main_langb Then
			HUD_PrintLog(para)
		EndIf
		DebugLog "Print "+para
	Case "apptitle" ; apptitle titel$
		AppTitle Prs_GetPara(file,Prs_pointer)
	Case "set_activity"
		hand		= Prs_GetPara(file,prs_pointer)
		vo.prs_void = Object.prs_void(hand)
		value		= Prs_GetPara(file,prs_pointer)
		vo\active	= value
		DebugLog "set trigger "+Handle(vo)+" to "+value
	Case "get_activity"
		hand		= Prs_GetPara(file,prs_pointer)
		vo.prs_void = Object.prs_void(hand)
		retur		= vo\active
		DebugLog "the activity of trigger"+hand+" is: "+retur
	Case "rand"
		value%		= Rand(Prs_GetPara(file,prs_pointer),Prs_GetPara(file,prs_pointer))
		retur$		= value
		DebugLog "rand: "+retur
	Case "rnd"
		valu#		= Rnd#(Prs_GetPara(file,prs_pointer),Prs_GetPara(file,prs_pointer))
		retur$		= valu#
		DebugLog "rnd: "+valu
	Case "delay"
		time		= Prs_GetPara(file,prs_pointer)
		v\delaying	= prs_pointer
		v\lastupdate= MilliSecs()+time-v\updaterate
	Case "loadaem"
		path$		= Prs_GetPara(file,prs_pointer)
		parent		= Prs_GetPara(file,prs_pointer)
		retur$		= AEM_Load(path$,parent)
	Case "freeaem"
		entity		= Prs_GetPara(file,prs_pointer)	
		AEM_FreeAEM(entity)
	Case "loadafx"
		path$		= Prs_GetPara(file,prs_pointer)
		parent		= Prs_GetPara(file,prs_pointer)
		retur$		= FX_LoadAFX(path$,parent)
	Case "freeafx"
		entity		= Prs_GetPara(file,prs_pointer)	
		FX_FreeAFX(entity)
		DebugLog "delete afx: "+entity
	Case "registerobject"
		entity		= Prs_GetPara(file,prs_pointer)	
		;o.obj		= Obj_New.obj()
		;o\entity	= entity
	Case "rotate"
		entity		= Prs_GetPara(file,prs_pointer)
		pitch#		= Prs_GetPara(file,prs_pointer)
		yaw#		= Prs_GetPara(file,prs_pointer)
		roll#		= Prs_GetPara(file,prs_pointer)
		RotateEntity entity,pitch,yaw,roll
		DebugLog "rotate "+entity+" to: "+pitch+","+yaw+","+roll
	Case "position"
		entity		= Prs_GetPara(file,prs_pointer)
		x#			= Prs_GetPara(file,prs_pointer)
		y#			= Prs_GetPara(file,prs_pointer)
		z#			= Prs_GetPara(file,prs_pointer)
		PositionEntity entity,x,y,z
	Case "scale"
		entity		= Prs_GetPara(file,prs_pointer)
		x#			= Prs_GetPara(file,prs_pointer)
		y#			= Prs_GetPara(file,prs_pointer)
		z#			= Prs_GetPara(file,prs_pointer)
		ScaleEntity entity,x,y,z
	Case "playerdistto"
	
	Case "source"
		retur		= Prs_CurrEvent\Source
		
	Case "get_flags_team"
		f.flag = Fla_FindFlagByID(Prs_GetPara(file,prs_pointer))
		retur		= f\team
		
	Case "quake"
		;cam_quake	= Prs_GetPara(file,prs_pointer)
		
	Case "end"
		Prs_CloseScript(v\file)
		DebugLog "end of skript "+ v\file
	Case "wingame"
		;main_won = 1
	Default
		If Len(token)>1 Then RuntimeError "Unknown Script Command: "+Left(token,Len(token)-1)
	End Select
	
	Return retur$
End Function


Function Prs_ParseScript(stream)
	size = 0
	bank = CreateBank(size+1)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			;Select Lower(paras[0])
			;End Select
		Else
			Select lin$
			Case "begin_inline"
				Repeat
					lin$ = Trim(ReadLine(stream))
					If lin$ = "end_inline" Then Exit
					
					ResizeBank bank,size+Len(lin)+2
					
					For i = 1 To Len(lin)
						PokeByte bank,size+i-1,Asc(Mid(lin,i,1))
					Next
					
					size = size + Len(lin)
				Until Eof(stream)
			End Select
		EndIf
	Until lin="}"
	
	file = Prs_PreCompile(bank)
	Prs_Compile(file)
	f.prs_file = New prs_file
	f\file = file
End Function

Function Prs_PreCompile(file)
	Prs_Pointer = -1
	Print path$+", "+path2
	
	size = BankSize(file)
	
	out = CreateBank(size)
	DebugLog BankSize(out)
	
	incode = 1
	
	Prs_VarID = 0
	
	Repeat
		token$ = Prs_GetToken(file,Prs_Pointer)
		If Prs_Pointer >= size-1
			Exit
		Else
			DebugLog token
			
			;If Right(token,1) = Chr(34) Then incode = 1-incode
			If (Asc(Right(token,1)) > 31 And Asc(Left(token,1))>32)
				If PeekByte(out,opos) = 32 And Prs_AscIsSeperator(Asc(Left(token,1)))
					opos = opos - 1
					RuntimeError ""
				EndIf
				If Right(token,1) = "$" Then
					t = 0
					For v.prs_var = Each prs_var
						If v\name = Left(token,Len(token)-1) Then token = Str(v\id) + "$" : t=1 :  Exit
					Next
					If t = 0 Then
						v.prs_var = New prs_var
						v\name	= Left(token,Len(token)-1)
						v\id	= Prs_VarID
						Prs_VarID = Prs_VarID + 1
						token	= Str(v\id) + "$"
					EndIf
				EndIf
				Util_PokeSting out,opos,token$
				opos = opos + Len(token$)
			EndIf
		EndIf
	Forever
	Delete Each prs_var
	ResizeBank out,opos+1
	
	FreeBank file
	
	Return out
End Function

Function Prs_Compile(file)
	Prs_Pointer = -1
	size		= BankSize(File)
	
	Repeat
		token$ = Prs_GetToken(file,Prs_Pointer)
		If Prs_Pointer >= size-1
			Exit
		Else
			DebugLog token
			
			eot$ = Right(token,1)
			
			Select token
			Case "timer", "timer "
				v.prs_void	= New prs_void
				v\typ		= PRS_TIMER
				v\name$		= Prs_GetToken$(file,prs_pointer)
				v\name$		= LSet(v\name,Len(v\name)-1)
				v\file		= file
				
				Prs_Pointer = Util_FindNext(file,prs_pointer-1,Asc("("))
				
				v\active	= Prs_GetPara$(file,prs_pointer)
				v\updaterate= Prs_GetPara$(file,prs_pointer)
				
				v\start		= Util_FindNext(file,prs_pointer,Asc("{"))
				
				DebugLog v\name+","+v\active+","+v\updaterate
			Case "event", "event "
				v.prs_void	= New prs_void
				v\typ		= PRS_EVENT
				v\name$		= Prs_GetToken$(file,prs_pointer)
				v\name$		= LSet(v\name,Len(v\name)-1)
				v\file		= file
				
				Prs_Pointer = Util_FindNext(file,prs_pointer-1,Asc("("))
				
				v\active	= Prs_GetPara$(file,prs_pointer)
				v\condition = Prs_GetPara$(file,prs_pointer)
				v\csource$	= Prs_GetPara$(file,prs_pointer)
				v\updaterate= 999999999
				
				v\start		= Util_FindNext(file,prs_pointer,Asc("{"))
				
				DebugLog v\name+","+v\active+","+v\updaterate
			End Select
			
			Select eot
			Case "{"
				brackdepth	= brackdepth + 1
				
				b.prs_brackets= New prs_brackets
				b\file		= file
				b\startpos	= prs_pointer
				b\endpos	= -1
				b\depth		= brackdepth
			Case "}"
				For b.prs_brackets = Each prs_brackets
					If b\depth = brackdepth And b\endpos = -1 And b\file = file
						b\endpos = prs_pointer
						For v.prs_void = Each prs_void
							If b\startpos = v\start And b\file = v\file
								v\ende = b\endpos
							EndIf
						Next
						DebugLog "Brackets:"+b\startpos+", "+b\endpos
					EndIf
				Next
				brackdepth	= brackdepth - 1
			End Select
		EndIf
	Forever
	Return file
End Function

Function Prs_GetToken$(bank,pos)
	Repeat
		pos = pos + 1
		b = PeekByte(bank,pos)
		txt$ = txt + Chr(b)
		If Prs_AscIsSeperator(b)
			If b = 34 Then
				pos2 = Util_FindNext(bank,pos,34)
				txt = txt + Util_PeekString$(bank,pos+1,pos2)
				pos = pos2
			ElseIf b = 92
				txt = Left(txt,Len(txt)-1)
				pos = Util_FindNext(bank,pos,92)
			EndIf
			Prs_Pointer = pos
			Prs_LToken	= txt
			Return txt
		EndIf
	Forever
	Prs_Pointer = pos
End Function


; Zu "startedwith":
Const PRS_PLSTART	= 0 ; (,);
Const PRS_PLBRACKET	= 1 ; []
Const PRS_PLCOMP	= 2 ; and or xor
Const PRS_PLBOOL	= 3 ; = > <
Const PRS_PLSTRING	= 4 ; .
Const PRS_PLADDI	= 5 ; +-
Const PRS_PLMULTI	= 6 ; */
Const PRS_PLPOWER	= 7 ; ^

Function Prs_GetPara$(bank,pos,startedwith=0);*
	Prs_Pointer = pos
	
	Repeat
		token$ = Prs_GetToken(bank,Prs_Pointer)
		eot$ = Right(token,1)
		
		Select eot
		Case Chr(34) ; varcontent
			para$ = Mid(token,2,Len(token)-2)
			
			;variables
		Case "$" ; (global)
			var$ = Left(token,Len(token)-1)
			
			para$ = Vars(var)
		Case "@" ; function
			sub$ = Left(token,Len(token)-1)
			id = Handle(Prs_FindVoid(sub,bank))
			para = id
			
			; command
		Case " ","(" 
			para = Prs_InterpretCommand$(bank,token,Null)
		
			; concat
		Case "."
			If startedwith > PRS_PLSTRNG Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			para = para + Prs_GetPara(bank,Prs_Pointer,PRS_PLSTRING)

			; maths
		Case "+"
			If startedwith > PRS_PLADDI Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			para = Float(para) + Float(Prs_GetPara(bank,Prs_Pointer,PRS_PLADDI))
		Case "-"
			If startedwith > PRS_PLADDI Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			para = Float(para) - Float(Prs_GetPara(bank,Prs_Pointer,PRS_PLADDI))
		Case "*"
			If startedwith > PRS_PLMULTI Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			para = Float(para) * Float(Prs_GetPara(bank,Prs_Pointer,PRS_PLMULTI))
		Case "/"
			If startedwith > PRS_PLMULTI Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			para = Float(para) / Float(Prs_GetPara(bank,Prs_Pointer,PRS_PLMULTI))
		Case "^"
			If startedwith > PRS_PLPOWER Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			para = Float(para) ^ Float(Prs_GetPara(bank,Prs_Pointer,PRS_PLPOWER))
		Case "="
			If startedwith > PRS_PLBOOL Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			para = (para = Prs_GetPara(bank,Prs_Pointer,PRS_PLBOOL))
		Case ">"
			If startedwith > PRS_PLBOOL Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			para = (Float(para) > Float(Prs_GetPara(bank,Prs_Pointer,PRS_PLBOOL)))
		Case "<"
			If startedwith > PRS_PLBOOL Then
				Prs_Pointer = Prs_Pointer - 1
				Return para
			EndIf
			If Float(para) < Float(Prs_GetPara(bank,Prs_Pointer,PRS_PLBOOL))
				para = 1
			Else
				para = 0
			EndIf
			;para = (para < Prs_GetPara(bank,Prs_Pointer,PRS_PLBOOL))
			
			;compwords
		Case " "
			Select token
			Case "and "
				If startedwith > PRS_PLCOMP Then
					Prs_Pointer = Prs_Pointer - 1
					Return para
				EndIf
				para = (Int(para) And Int(Prs_GetPara(bank,Prs_Pointer,PRS_PLCOMP)))
			Case "or "
				If startedwith > PRS_PLCOMP Then
					Prs_Pointer = Prs_Pointer - 1
					Return para
				EndIf
				If (Int(para) Or Int(Prs_GetPara(bank,Prs_Pointer,PRS_PLCOMP))) Then
					para = 1
				Else
					para = 0
				EndIf
			Case "xor "
				If startedwith > PRS_PLCOMP Then
					Prs_Pointer = Prs_Pointer - 1
					Return para
				EndIf
				para = (para Xor Prs_GetPara(bank,Prs_Pointer,PRS_PLCOMP))
			End Select
			
			;brackets
		Case "["
			para = Prs_GetPara(bank,Prs_Pointer,PRS_PLBRACKET)
		Case "]"
			Return para$
		End Select
	Until eot = ")" Or eot = ";" Or eot = ","
	If startedwith > PRS_PLSTART Then Prs_Pointer = Prs_Pointer - 1
	Return para$
End Function

Function Prs_AscIsSeperator(b)
	Return (b < 48) Or (b > 57 And b < 65) Or (b > 90 And b < 95) Or (b > 122 );And b < 127)
	;b = 3 Or b = 13 Or (b > 31 And
End Function