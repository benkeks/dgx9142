
Type TTrack
	Field file$
	Field name$
	Field parameter$
End Type

Global music_volume# = 1
Global music_fade# = 1, music_fadeout
Global music_current=0, music_track.TTrack

Function Music_CreateLib(path$)
	Delete Each TTrack
	
	dir = ReadDir(path$)
	NextFile(dir)
	NextFile(dir)
	i = 0
	Repeat
		pfad$ = NextFile(dir)
		If pfad$="" Then Exit
		Select Util_StripType(pfad$)
		Case "wav", "ogg", "mp3"
			t.TTrack = New TTrack
			t\file	= path+pfad
			t\name	= Util_StripPath(pfad)
			DebugLog t\file
		End Select
		i = i + 1
	Until pfad$=""
	CloseDir dir
	
	music_track = t
	
	stream = ReadFile(path$+"special/music.inf")
	If stream <> 0 Then
		Repeat
			lin$ = ReadLine(stream)
			If Util_GetParas(lin$)
				t.TTrack = New TTrack
				t\file	= paras[1]
				t\name	= Util_StripPath(t\file)
				t\parameter	= paras[0]
			EndIf
		Until Eof(stream)
		CloseFile stream
	EndIf
	
End Function

Function Music_Update()
	If music_volume > 0.01
		If music_current = 0 Or music_fade <= 0 Then
			If music_fadeout Then 
				Music_SetTrack(music_track)
			Else
				Music_NextTrack()
			EndIf
		Else
			If ChannelPlaying(music_current) = 0 Then
				music_current = 0
			EndIf
		EndIf
		If music_fade < 1 And music_current <> 0 Then ChannelVolume music_current, music_volume*music_fade
		If music_fadeout Then music_fade = music_fade - .02*main_gspe
	EndIf
End Function

Function Music_NextTrack()
	Repeat
		music_track = After music_track
		If music_track = Null Then music_track = First TTrack
	Until music_track\parameter = "" 
	;If music_track <> Null
	;	music_current = PlayMusic(music_track\file)
	;	ChannelVolume music_current, music_volume
	;EndIf
	music_fade = 1
	music_fadeout = 1
End Function

Function Music_SetTrack(track.TTrack)
	If ChannelPlaying(music_current) Then StopChannel music_current
	music_track = track
	If music_track = Null Then music_track = First TTrack
	If music_track <> Null
		music_current = PlayMusic(music_track\file)
		ChannelVolume music_current, music_volume
	EndIf
	music_fade = 1
	music_fadeout = 0
End Function

Function Music_SpecialTrack(parameter$)
	For m.TTrack = Each TTrack
		If m\parameter = parameter Then
			music_track = m
			music_fade = 1
			music_fadeout = 1
			Exit
		EndIf
	Next
End Function

Function Music_SetVolume(vol#)
	music_volume = vol#
	If ChannelPlaying(music_current) Then
		ChannelVolume music_current, music_volume
		If vol < 0.01 Then StopChannel music_current
	EndIf
End Function