; Behandelt Lautstärke-Regelung
; by INpac

; Und Soundentities
; by Inarie ;)

Global Aud_globalVolume#	= 1
Global Aud_speechVolume#	= 1
Global Aud_effectsVolume#	= 1

Function Aud_LoadAudio()
	Util_CheckFile(datad$+"Audio.ini")
	stream = ReadFile(datad$+"Audio.ini")
	Repeat
		lin$ = ReadLine(stream)
		If Util_GetParas(lin$)
			Select Lower(paras[0])

			; Moving
			Case "globalvolume"
				Aud_globalVolume	= paras[1]
			Case "speechvolume"
				Aud_speechVolume	= paras[1]
			Case "effectsvolume"
				Aud_effectsVolume	= paras[1]

			End Select
		EndIf
	Until Eof(stream)
	CloseFile stream
End Function

;-----------------------------------------------

Type sfx
	Field chan
	Field piv
	Field sound
	Field Modus = 1 ; 1: start, 2: loop
	Field time#
	Field vol# = 1
;	Field effect = 1 ; 1: effect, 2: speech
End Type

Function Aud_Update()
	For s.sfx = Each sfx
		If s\time <= 0
			If ChannelPlaying(s\chan) = 0
				If s\modus > 0 Then
					s\chan = EmitSound(s\sound,s\piv)
					ChannelVolume s\chan,s\vol
					If s\modus = 1 Then s\modus = 0
				Else
					;FreeSound s\sound
					FreeEntity s\piv
					Delete s.sfx
				EndIf
			EndIf
		Else
			s\time = s\time - main_mscleft
		EndIf
	Next
End Function

Function Aud_CreateSFX(sound,par,modus=1)
	s.sfx = New sfx
	s\piv = CreatePivot(par)
	EntityParent s\piv,0
	s\sound = sound
	s\modus = modus
	s\vol = 1
	
End Function

Function Aud_ParseSFX(stream,par)
	s.sfx = New sfx
	Repeat
		lin$ = Trim(ReadLine(stream))
		Select lin$
		Case "aem{"
			AEM_Parse(stream,mesh)
		Case "child{"
			FX_ParseAFX(stream,mesh)
		Default
			If Util_GetParas(lin$)
				Select paras[0]
				Case "sound"
					s\sound = Load3DSound(paras[1])
				Case "modus"
					s\modus	= paras[1]
				Case "pitch"
					SoundPitch s\sound,paras[1]
				Case "effect"
					Select paras[1]
					Case 1
						s\vol = s\vol * Aud_EffectVolume * Aud_Globalvolume
					Case 2
						s\vol = s\vol * Aud_SpeechVolume * Aud_Globalvolume
					End Select
				Case "volume"
					s\vol = Float(paras[1])
					SoundVolume s\sound,paras[1]
					If s\vol = 100 Then par = cam_cam
				End Select
			EndIf
		End Select
	Until lin="}"
	s\piv	= CreatePivot(par)
	
	Return s\piv
End Function

Function Aud_Clear()
	For s.sfx = Each sfx
		;FreeSound s\sound
		FreeEntity s\piv
		Delete s.sfx
	Next
End Function