Global main_width=1024
Global main_height=768
Global main_hwidth=512
Global main_hheight=384
Global main_ratio#
Global main_bit=32
Global main_mode=2

Global main_vsync

Global main_hwmultitex = 1
Global main_dither
Global main_wbuffer
Global main_aalias

Global main_detail# = 1
Global main_gamma
Global main_highpartR
Global main_dot3, main_bgdetail

Global main_bloom#=0
Global main_texdetail = 1

Global main_showminimap = 1
Global main_showminiplayer = 1
Global main_particledetail# = 1

;-----------------

Function Main_InitGfx()
	If FileType(datad$+"graphics.ini")<>1 Then
		Util_CheckFile(datad$+"DEFAULT/graphics.ini",0)
		CopyFile datad$+"DEFAULT/graphics.ini", datad$+"graphics.ini"
	EndIf

	stream = ReadFile(datad$+"graphics.ini")
	Repeat
		lin$ = ReadLine(stream)
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "width"
				main_width = paras[1]
				main_hwidth= main_width/2
			Case "height"
				main_height = paras[1]
				main_hheight= main_height/2
			Case "bit"
				main_bit	= paras[1]
			Case "mode"
				If main_mode <> 222 Then main_mode	= paras[1]
				
			Case "vsync"
				main_vsync	= paras[1]
			
			Case "hwmultitex"
				main_hwmultitex	= paras[1]
			Case "dither"
				main_dither		= paras[1]
			Case "wbuffer"
				main_wbuffer	= paras[1]
			Case "antialias"
				main_aalias		= paras[1]
	
			Case "detail"
				main_detail		= paras[1]
			Case "gamma"
				main_gamma		= paras[1]
			Case "highparticlerate"
				main_highpartR	= paras[1]
			Case "bloom","hdr"
				main_bloom		= paras[1]
			Case "texturedetail"
				main_texdetail	= paras[1]
			Case "backgrounddetail"
				main_bgdetail	= paras[1]
			Case "dot3"
				main_dot3		= paras[1]
				
			Case "showminimap"
				main_showminimap	= paras[1]
			Case "showminiplayer"
				main_showminiplayer	= paras[1]
			Case "particledetail"
				main_particledetail	= paras[1]
			End Select
		EndIf
	Until Eof(stream)
	CloseFile stream
	
	If main_mode = 222 Then main_mode = 2 ; windowed per commandline
	
	If main_mode = 2 Then
		If Not Windowed3D() Then
			DebugLog "Windowed mode not supported! Switch to fullscreen."
			main_mode = 1
		EndIf
	EndIf
	
	If Not GfxMode3DExists(main_width, main_height, main_bit) Then 
		main_width = 1024
		main_height = 768
		main_bit = 0
		main_hwidth= main_width/2
		main_hheight= main_height/2
	EndIf
	
	main_ratio = Float(main_height) / main_width
	
	If main_mode = 2
		Graphics3D main_width,main_height,main_bit,2
	Else
		Graphics3D main_width,main_height,main_bit,main_mode
	EndIf
	SetBuffer BackBuffer()
	
	HWMultiTex main_hwmultitex
	Dither main_dither
	WBuffer main_wbuffer
	AntiAlias main_aalias
End Function

Function Main_SaveGfx()
	stream = WriteFile(datad$+"graphics.ini")
	WriteLine stream, "width = "+main_width
	WriteLine stream, "height = "+main_height
	WriteLine stream, "bit = "+main_bit
	WriteLine stream, "mode = "+main_mode
	WriteLine stream, "vsync = "+main_vsync
	WriteLine stream, "hwmultitex = "+main_hwmultitex
	WriteLine stream, "dither = "+main_dither
	WriteLine stream, "wbuffer = "+main_wbuffer
	WriteLine stream, "antialias = "+main_aalias
	WriteLine stream, "detail = "+main_detail
	WriteLine stream, "gamma = "+main_gamma
	WriteLine stream, "bloom = "+main_bloom
	WriteLine stream, "texturedetail = "+main_texdetail
	WriteLine stream, "dot3 = "+main_dot3
	WriteLine stream, "backgrounddetail = "+main_bgdetail
	WriteLine stream, "showminimap = "+main_showminimap
	WriteLine stream, "showminiplayer = "+main_showminiplayer
	WriteLine stream, "particledetail = "+main_particledetail
	CloseFile stream
End Function

Function Main_ParseDetail(stream,lin$)
	lin$ = Util_Trim2(Lower(lin))
	Util_GetParas(Right(lin$,Len(lin$)-1),":")
	showit = 0
	Select paras[0]
	Case "backgrounddetail"
		If main_bgdetail = paras[1] Then showit = 1
	Case "dot3"
		If main_dot3 = paras[1] Then showit = 1
	End Select
	
	While showit = 0
		If ">" = Util_Trim2(ReadLine(stream)) Or Eof(stream) Then showit = 1
	Wend
End Function