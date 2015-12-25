; inaries kleine b3d-gui-lib

Type gadget
	Field mesh
	Field typ ; 1 = panel ; 2 = button, 3 = progressbar
	Field active ; -1 = hidden ; 0 = inaktiv ; 1 = aktiv
	Field hotkey
	
	Field vis#
	
	Field mhandle
	Field turn#, tturn#
	
	Field x#,y#,w#,h#
	
	Field value#,value2#
	Field mesh2, meshicon
	Field meshhover, hover#
	Field hoverinfo$
End Type

Global gui_event
Global gui_eventsource

Global gui_par
Global gui_mesh

Function GUI_Init(cam)
	gui_par = CreatePivot(cam)
	PositionEntity gui_par,0,0,100
	
	gui_mesh		= CreateMesh(parent)
	sur			= CreateSurface(gui_mesh)	
	Local vert[3]
	vert[0]	= AddVertex(sur,	0,0,0,		0,0)
	vert[1]	= AddVertex(sur,	1,0,0,		1,0)
	vert[2]	= AddVertex(sur,	0,-1,0,		0,1)
	vert[3]	= AddVertex(sur,	1,-1,0,		1,1)
	AddTriangle sur, vert[0], vert[2], vert[1]
	AddTriangle sur, vert[1], vert[2], vert[3]
	
	
	gui_event = 0
	gui_eventsource = 0
End Function


Function Gui_Clear()
	FreeEntity gui_mesh
	FreeEntity gui_par
	Delete Each gadget
End Function

Function GUI_AddGadget.gadget(typ,x#,y#,width,height,parent=0,order=0,active=1,zoff#=0)
	If parent = 0 Then parent = gui_par
	
	g.gadget	= New gadget
	g\typ		= typ
	
	g\x			= x
	g\y			= y
	g\w			= width
	g\h			= height
	
	g\mesh		= CopyMesh(gui_mesh, parent)
	
	ScaleMesh g\mesh,width,height,1
	EntityFX g\mesh,1+8+16
	EntityOrder g\mesh,-10-order
	PositionEntity g\mesh,x,y,zoff#
	
	g\meshhover = CopyEntity(g\mesh,g\mesh)
	PositionEntity g\meshhover,0,0,0
	EntityBlend g\meshhover,3
	EntityFX g\meshhover,1+8+16
	EntityOrder g\meshhover,-11-order
	EntityAlpha g\meshhover,0
	
	GUI_SetGadgetActivity(g,active)
	
	Return g
End Function

Function GUI_SetCaption(gh, caption$, r=255,gr=255,b=255, size#=.5)
	g.gadget = Object.gadget(gh)
	If g\mesh2 <> 0 Then FreeEntity g\mesh2
	If caption <> ""
		g\mesh2 = Txt_Text(caption$, hud_font, g\mesh)
		ScaleMesh g\mesh2,size,size,size
		MoveEntity g\mesh2,4+6*(g\meshicon<>0),-g\h/2-MeshHeight(g\mesh2)/2,0
		EntityColor g\mesh2,r,gr,b
		EntityOrder g\mesh2,-14
	EndIf
End Function

Function GUI_SetHotKey(gh, key)
	g.gadget = Object.gadget(gh)
	g\hotkey = key
End Function

Function GUI_SetHoverInfo(gh, hoverinfo$)
	g.gadget	= Object.gadget(gh)
	g\hoverinfo	= hoverinfo
End Function

Function GUI_SetMidHandle(gh)
	g.gadget = Object.gadget(gh)
	PositionMesh g\mesh, -g\w/2.0, g\h/2.0,0
	g\x = g\x-g\w/2
	g\y = g\y+g\h/2
	g\mhandle = 1
End Function

Function GUI_SetIcon(gh, mesh)
	g.gadget = Object.gadget(gh)
	If g\meshicon <> 0 Then FreeEntity g\meshicon
	If caption <> ""
		g\meshicon = CopyEntity(mesh, g\mesh)
		;ScaleMesh g\meshicon,size,size,size
		MoveEntity g\meshicon,2,-g\h/2,0
		EntityOrder g\meshicon,-14
	EndIf
End Function

Function GUI_SetGadgetActivity(g.gadget,active=1)
	g\active = active
	g\hover = 0
	ShowEntity g\meshhover
	Select active
	Case -1
		HideEntity g\mesh
		g\vis = 0
	Case 0
		ShowEntity g\mesh
		EntityColor g\mesh,128,128,128
	Case 1
		ShowEntity g\mesh
		EntityColor g\mesh,255,255,255
	Case 2
		ShowEntity g\mesh
		EntityColor g\mesh,255,255,255
	End Select
End Function

Function GUI_SetGadActivity(gh,active=1)
	g.gadget = Object.gadget(gh)
	g\active = active
	g\hover = 0
	ShowEntity g\meshhover
	Select active
	Case -1
		HideEntity g\mesh
		g\vis = 0
	Case 0
		ShowEntity g\mesh
		EntityColor g\mesh,128,128,128
	Case 1
		ShowEntity g\mesh
		EntityColor g\mesh,255,255,255
	Case 2
		ShowEntity g\mesh
		EntityColor g\mesh,255,255,255
	End Select
End Function

Function GUI_SetTurn(gh, turn#)
	g.gadget = Object.gadget(gh)
	g\tturn = turn
End Function

Function GUI_GadTexture(gh,tex)
	g.gadget = Object.gadget(gh)
	EntityTexture g\mesh,tex
	EntityTexture g\meshhover,tex
End Function

Function GUI_Update()
	For g.gadget = Each gadget
		GUI_UpdateGadget(g)
	Next
End Function

Function GUI_SetVis()
	For g.gadget = Each gadget
		g\vis = 0
	Next
End Function

Function GUI_UpdateGadget(g.gadget, zoom=100)
	gmx = (mx-main_hwidth)*zoom/main_hwidth-hud_showcsanim
	gmy = (-my+main_hheight)*zoom/main_hheight*main_ratio
	
	RotateEntity g\mesh, 0,0,g\turn
	g\turn = g\turn + (g\tturn - g\turn) * .2
	
	If g\active >= 0 And g\vis < 1 Then
		EntityAlpha g\mesh, g\vis
		If g\mesh2 <> 0 Then EntityAlpha g\mesh2, g\vis
		g\vis = g\vis + .01 * main_gspe
	ElseIf g\active >= 0
		g\vis = 1
		EntityAlpha g\mesh, g\vis
		If g\mesh2 <> 0 Then EntityAlpha g\mesh2, g\vis
	EndIf
	
	EntityAlpha g\meshhover,g\hover*g\vis*.6
	
	If g\active = 1
		EntityBlend g\mesh,1
		Select g\typ
		Case 1
			
		Case 2
			If gmx > g\x And gmx < g\x+g\w And gmy < g\y And gmy > g\y-g\h And hud_mode<>0 And g\vis > .5
				EntityBlend g\mesh,1
				If g\hoverinfo <> "" Then Hud_SetHoverInfo(hud_comhoverinfo,mx,my,g\hoverinfo)
				If g\hover < 1 Then g\hover = g\hover + .1*main_gspe
				If mh = 1 Then
					PlaySound hud_button
					gui_event		= 401
					mh = 0
					gui_eventsource = Handle(g)
				EndIf
			Else
				EntityBlend g\mesh,1
				If g\hover > 0 Then g\hover = g\hover - .1*main_gspe
			EndIf
			If Inp_KeyHit(g\hotkey) And hud_mode<>0 And g\vis > .5
				PlaySound hud_button
				gui_event		= 401
				gui_eventsource = Handle(g)
			End If
		Case 3
			;If g\mesh2 Then FreeEntity g\mesh2
			scale# = g\value/g\value2
			If scale# < 0 Then scale# = 0
			If Abs(g\w) > Abs(g\h)
				ScaleEntity g\mesh,scale#,1,1
				If g\h > 10
					;g\mesh2 = Txt_Text(Int(scale*100)+"%", Hud_font, g\mesh)
					g\mesh2 = Txt_UpdateText(g\mesh2, Int(scale*100)+"%", hud_font, g\mesh)
					PositionEntity g\mesh2,.5,-g\h,0
					;EntityParent g\mesh2,0
					ScaleEntity g\mesh2,1.50/scale,1.3,1
					EntityOrder g\mesh2,-14
				EndIf
			Else
				ScaleEntity g\mesh,1,scale#,1
				g\mesh2 = Txt_UpdateText(g\mesh2, Int(scale*100)+"%", hud_font, g\mesh)
				PositionEntity g\mesh2,g\w,.5,0
				RotateEntity g\mesh2,0,0,90
				;EntityParent g\mesh2,0
				ScaleEntity g\mesh2,1.50/scale,1.3,1
				EntityOrder g\mesh2,-14
			EndIf
		End Select
	ElseIf g\active = 2
		If g\hover < 1 Then g\hover = g\hover + .1*main_gspe
		If gmx > g\x And gmx < g\x+g\w And gmy < g\y And gmy > g\y-g\h And hud_mode<>0 And g\vis > .5 Then
			Hud_SetHoverInfo(hud_comhoverinfo,mx,my,g\hoverinfo)
		EndIf
		EntityBlend g\mesh,1
	EndIf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D