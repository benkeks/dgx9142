Type hud_hoverinfo
	Field txt$
	Field bg, tmesh
	Field fade#
	Field timer
End Type

Function Hud_CreateHoverInfo.hud_hoverinfo(x#=0,y#=0, txt$="")
	h.hud_hoverinfo = New hud_hoverinfo
	
	h\bg = CreateSprite(cc_cam)
	HandleSprite h\bg,-1,1
	EntityColor h\bg,0,0,0
	EntityOrder h\bg,-20
	h\tmesh = CreateMesh(h\bg)
	
	Hud_SetHoverInfo(h, x,y, txt)
	Return h
End Function

Function Hud_SetHoverInfo(h.hud_hoverinfo, x#,y#, txt$)
	If txt <> "" And h\txt <> txt
		Local width = Util_MinMax(main_width, 1024,2000)
		Local height = main_height * width / main_width
		Local hwidth = width / 2, hheight = height/2
		
		FreeEntity h\tmesh
		h\tmesh = Txt_Text(txt$, Hud_font, h\bg,0,150)
		ScaleEntity h\tmesh,2,2,2
		EntityOrder h\tmesh,-21
		wid# = MeshWidth(h\tmesh)
		hei# = MeshHeight(h\tmesh)
		ScaleSprite h\bg,wid,hei
		MoveEntity h\tmesh,0,-26,0
		If x+wid*2 > width Then x = x-wid*2
		If y+hei*2 > height Then y = y-hei*2
		PositionEntity h\bg,x-hwidth,hheight-y,hwidth
		If Left(h\txt,5) <> Left(txt,5) h\fade = -.5
	EndIf
	h\timer = 800
	h\txt = txt
End Function

Function Hud_UpdateHoverInfo()
	For h.hud_hoverinfo = Each hud_hoverinfo
		EntityAlpha h\bg, h\fade*.7
		EntityAlpha h\tmesh, h\fade
		If h\txt = ""
			If h\fade > 0 Then h\fade = h\fade - .02 * main_gspe
		Else
			If h\fade < 1 Then h\fade = h\fade + .02 * main_gspe
			h\timer = h\timer - main_mscleft
			If h\timer < 0 Then h\txt=""
		EndIf
	Next
End Function

Function Hud_RemoveHoverInfo(h.hud_hoverinfo)
	FreeEntity bg
	Delete h
End Function

Function Hud_ClearHoverInfo()
	For h.hud_hoverinfo = Each hud_hoverinfo
		Hud_RemoveHoverInfo(h)
	Next
End Function