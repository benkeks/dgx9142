Global main_font

Global main_lang$ = "de_" , main_langb$ = "de"
Global main_name$ = "player"
Global main_port = 9142
Global main_musicdir$ = "SFX/MUSIC/"

Global main_shadows = 0

Function Main_Load()
	main_font = LoadFont("Verdana",14)
	SetFont main_font
	
	Main_LoadSettings()
	Lang_Load(datad+"LANGUAGES/"+Left(main_lang,Len(main_lang)-1)+".ini")
	CC_LoadControls(datad$+"controls_"+cc_profile+".ini")
	Music_CreateLib(main_musicdir)
End Function

Function Main_LoadSettings()
	Util_CheckFile(datad$+"settings.ini",0)
	stream = ReadFile(datad$+"settings.ini")
	Repeat
		lin$ = ReadLine(stream)
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "language"
				main_lang 		= paras[1]+"_"
				main_langb		= paras[1]
			Case "name"
				main_name 		= paras[1]
			Case "port"
				main_port 		= paras[1]
			Case "music"
				main_musicdir	= paras[1]
			Case "music_volume"
				music_volume	= paras[1]
			Case "controlprofile"
				cc_profile		= paras[1]
				If cc_profile <> 0 Then cc_profile = 1
			End Select
		EndIf
	Until Eof(stream)
	CloseFile stream
	
	If main_name = "" Then main_name = "player"
End Function

Function Main_SetLanguage(lang$)
	main_lang$ = lang+"_"
	main_langb = lang
	Main_SaveSettings()
End Function

Function Main_SaveSettings()
	stream = WriteFile(datad$+"settings.ini")
	WriteLine stream, "language = "+ Left(main_lang,Len(main_lang)-1)
	WriteLine stream, "name = "+main_name
	WriteLine stream, "port = "+main_port
	WriteLine stream, "music = "+main_musicdir
	WriteLine stream, "music_volume = "+music_volume
	WriteLine stream, "controlprofile = "+cc_profile
	CloseFile stream
End Function