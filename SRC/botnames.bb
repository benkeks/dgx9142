Type botname
	Field name$
	Field team
	Field used
End Type

Function KI_ReadBotnames()
	For i = 1 To 2
		Util_CheckFile(datad$+"botnames_"+i+".ini",0)
		stream = ReadFile(datad$+"botnames_"+i+".ini")
		Repeat
			lin$ = ReadLine(stream)
			If lin$ <> "" Then 
				b.botname = New botname
				b\name = lin
				b\team = i
				b\used = 0
			EndIf
		Until Eof(stream)
		CloseFile stream
	Next
	
	;shuffle
	For i = 0 To 300
		For b.botname = Each botname
			If Rand(0,2) = 1 Then Insert b After Last botname
		Next
	Next
End Function

Function KI_BotName$(team)
	name$ = ""
	count = 0
	Repeat
		For b.botname = Each botname
			If b\team = team And b\used <= count Then
				name = b\name
				b\used = b\used + 1
				Exit
			EndIf
		Next
		count = count + 1
	Until name$ <> ""
	Return name$
End Function

Function KI_ResetBotnames()
	For b.botname = Each botname
		b\used = 0
	Next
End Function

Function KI_ClearBotnames()
	Delete Each botname
End Function