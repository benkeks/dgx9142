
Global lang_win$
Global lang_quit_game$

Global lang_quit_game_button$
Global lang_commander_mode$

Global lang_pause$

Global lang_continue$
Global lang_stop_spawn$
Global lang_attack$
Global lang_moveto$

Global lang_flag_being_taken$
Global lang_flag_taken$
Global lang_flag_being_taken_by_enemy$
Global lang_flag_taken_by_enemy$

Global lang_leaving_the_ground$

Global lang_menu_player_name$
Global lang_menu_error$
Global lang_more_recent_version$

Type TMLang
	Field entry$
	Field transl$
End Type



Function Lang_Load(file$)

	Util_CheckFile(file,0)
	stream = ReadFile(file)
	Repeat
		lin$ = ReadLine(stream)
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "win"
				lang_win = paras[1]
			Case "quit_game"
				lang_quit_game = paras[1]
			Case "quit_game_button"
				lang_quit_game_button$ = paras[1]
			Case "commander_mode"
				lang_commander_mode$ = paras[1]
			Case "pause"
				lang_pause = paras[1]
			Case "continue"
				lang_continue = Paras[1]
			Case "stop_spawn"
				lang_stop_spawn = paras[1]
			Case "moveto"
				lang_moveto = paras[1]
			Case "attack"
				lang_attack = paras[1]
			Case "flag_being_taken"
				lang_flag_being_taken = paras[1]
			Case "flag_being_taken_by_enemy"
				lang_flag_being_taken_by_enemy = paras[1]
			Case "flag_taken"
				lang_flag_taken = paras[1]
			Case "flag_taken_by_enemy"
				lang_flag_taken_by_enemy = paras[1]
			Case "leaving_the_ground"
				lang_leaving_the_ground = paras[1]
			Case "menu_error"
				lang_menu_error = paras[1]
			Case "menu_player_name"
				lang_menu_player_name = paras[1]
			Case "more_recent_version"
				lang_more_recent_version = paras[1]
			Default
				If Left(Lower(paras[0]),5) = "menu_" Then
					l.TMlang = New TMLang
					l\entry = Right(Lower(paras[0]),Len(paras[0])-5)
					l\transl = paras[1]
				EndIf
			End Select
		EndIf
	Until Eof(stream)
	CloseFile stream

End Function

Function Lang_Translate$(entry$)
	For l.TMLang = Each TMLang
		If l\entry = entry Then Return l\transl 
	Next
	Return entry$
End Function
