
Type TGadget
	Field x,y,width,height
	Field rx,ry
	Field caption$
	Field hint$
	Field typ ; 0 = fenster, 1 = button
	Field parent.TGadget
	Field anim#
	Field hide
	Field mesh
	Field handl ; handle des objekts
End Type

Type TWindow
	; erweitert TGadget
	Field mbg
	Field mcaption
End Type

Type TButton
	; erweitert TGadget
	Field mesh
	Field mcaption
	Field r,g,b
	Field hotkey
	Field OnClick$ ; gaaanz billiges event :)
End Type

Type TSlider
	; erweitert TGadget
	Field pos
	Field steps
	Field mesh, mcaption, mslider
	Field changing
	Field OnChange$
End Type

Type TCombo
	Field mesh
	Field smesh
	Field lmesh,lbgmesh
	Field lbganim#
	Field bgy#
	Field sel,seltext$
	Field seld
	Field OnChange$
End Type

Type TTick
	Field mesh
	Field mcaption
	Field set
	Field OnClick$
End Type

Type TKeySelect
	Field mesh
	Field mcaption
	Field key
	Field active
	Field OnChange$
End Type

Type TInput
	Field mesh
	Field mcaption
	Field mtext
	Field txt$
	Field mode
	Field active
	Field OnChange$
End Type

Type TList
	Field mesh,tmesh
	Field sel, seltext$
	Field clicktime
	Field OnChange$
	Field OnDoubleClick$
End Type

Const gtWindow		= 0
Const gtButton		= 1
Const gtSlider		= 2
Const gtCombo		= 3
Const gtTick		= 4
Const gtKeySelect	= 5
Const gtInput		= 6
Const gtList		= 7

Const imDefault		= 0
Const imNum			= 1
Const imNumIP		= 2

Global MGui_Piv
Global MGUI_mbgtex%
Global MGUI_BGtex_name$=gfxd$+"GUI/gameover.png"

Global MGui_Font
Global MGui_Click = LoadSound("SFX/GUI/button.wav")
Global MGui_KeyHit = LoadSound("SFX/GUI/ammo.mp3")

Global MGui_Hint.MGui_HoverInfo

Function MGui_Init() ; GUI zusammenbasteln :)
	MGui_Piv = CreatePivot(Menu_Cam)
	PositionEntity MGui_Piv,0,0,100	
	
	MGui_Font = Txt_LoadFont(gfxd$+"GUI/Font.png",1+2+8,4,10,gfxd$+"GUI/Font.inf")
	
	MGui_Hint = MGUI_CreateHoverInfo.MGUI_hoverinfo(1000,100,"")
End Function


Function MGui_Update()
	mx = MouseX()
	my = MouseY()
	mh = MouseHit(1)
	mlh = mh
	mrh = MouseHit(2)
	key = GetKey()
	MGui_UpdateHoverInfo()
	For g.TGadget = Each TGadget
		g\rx = g\x
		g\ry = g\y
		
		If g\parent <> Null Then
			TFormPoint 0,0,0,g\mesh,cam_cam
			g\rx = TFormedX() ;g\x + g\parent\rx
			g\ry = TFormedY();g\y + g\parent\ry
			If g\parent\anim = 0 
				If g\anim > 0 Then g\anim = g\anim - main_gspe*2 Else g\anim = 0 
				g\hide = g\parent\hide
			ElseIf g\parent\hide = 1
				g\hide = 1
				If g\anim < 100 Then g\anim = g\anim + main_gspe * 2 Else g\anim = 100
			EndIf
		Else
			If g\hide = 0
				If g\anim > 0 Then g\anim = g\anim - main_gspe*2 Else g\anim = 0 
			Else
				If g\anim < 100 Then g\anim = g\anim + main_gspe*2 Else g\anim = 100 
			EndIf
		EndIf
		
		If g\hint <> "" And g\anim = 0
			If mouse_X>g\rx-1 And mouse_x<g\rx+g\width+1 And mouse_y<g\ry+1 And mouse_y>g\ry-g\height-1 Then
				MGui_SetHoverInfo(MGui_Hint, mouse_x,mouse_y, g\hint)
			EndIf
		EndIf
		
		Select g\typ
		Case gtWindow
			w.TWindow = Object.TWindow(g\Handl)
			PositionEntity w\mbg, EntityX(w\mbg),EntityY(w\mbg),g\anim
			;ScaleEntity w\mbg,1-g\anim/100,1-g\anim/100,1-g\anim/100
			EntityAlpha w\mbg,(1-g\anim/100)*.5
		Case gtButton
			b.TButton = Object.TButton(g\Handl)
			
			EntityAlpha b\mesh,(1-g\anim/100)*.5
			PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),-g\anim*2
			
			HideEntity b\mcaption
			If g\anim = 0
				ShowEntity b\mcaption
				If mh = False And b\hotkey > 0 And KeyHit(b\hotkey) = True Then
					EntityColor g\mesh,225,225,225
					PlaySound MGui_Click
					MGui_Event(Handle(b),b\OnClick)
				ElseIf mouse_X>g\rx And mouse_x<g\rx+g\width And mouse_y<g\ry And mouse_y>g\ry-g\height Then
					EntityColor g\mesh,225,225,225
					If Not MouseDown(1) Then PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),-1
					If mh Then PlaySound MGui_Click PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),1 : MGui_Event(Handle(b),b\OnClick) : mh=0
				Else		
					EntityColor b\mesh,b\r,b\g,b\b
					PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),0
					;Color b\r/2,b\g/2,b\b/2
				EndIf
			ElseIf g\anim < 10
				EntityAlpha b\mesh,1
			EndIf
		Case gtSlider
			s.TSlider = Object.TSlider(g\Handl)
			EntityAlpha s\mesh,(1-g\anim/100)*.5
			PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),-g\anim*2
			HideEntity s\mcaption
			EntityColor s\mslider,150,150,150
			If g\anim = 0
				ShowEntity s\mcaption
				PositionEntity s\mslider,s\pos*g\width/(s\steps),1,0
				If s\changing = 0
					If mouse_X>g\rx-1 And mouse_x<g\rx+g\width+1 And mouse_y<g\ry+1 And mouse_y>g\ry-g\height-1 Then
						EntityColor s\mesh,225,225,225
						EntityColor s\mslider,100,150,250
						If mh Then s\changing = 1 : mh=0
					Else				
						EntityColor s\mesh,20,20,20
						PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),0
					EndIf
				Else
					s\pos = Int(Float(mouse_x-g\rx) * s\steps / g\width - 0.5)
					s\pos = Util_MinMax(s\pos,0,s\steps-1)
					If MouseDown(1) = 0 Then MGui_Event(Handle(s),s\OnChange) : PlaySound MGui_Click : s\changing = 0
					EntityColor s\mslider,100,150,250
				EndIf
			ElseIf g\anim < 10
				EntityAlpha s\mesh,1
			EndIf
		Case gtCombo
			c.TCombo = Object.TCombo(g\Handl)
			
			EntityAlpha c\mesh,(1-g\anim/100)*.5
			PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),-g\anim*2
			
			HideEntity c\smesh
			If g\anim = 0
				ShowEntity c\smesh
				If c\seld Then
					If c\lbganim < 100 Then
						c\lbganim = c\lbganim + 2*main_gspe
					Else
						c\lbganim = 100
						ShowEntity c\lmesh
					EndIf
					ScaleEntity c\lbgmesh,c\lbganim/100.0,1,1
					EntityAlpha c\lbgmesh,c\lbganim/200.0
					N = CountChildren(c\lmesh)
					point = 0
					Local yOffset# = Util_MinMax(- mouse_gh - (g\ry - n*g\height), 0, g\ry - 1)
					For i = 1 To N
						mesh = GetChild(c\lmesh,i)
						EntityColor mesh,255,255,255
						If mouse_x>g\rx+g\width And mouse_x<g\rx+g\width*2 And mouse_y < g\ry-g\height*(i-.5) + yOffset And mouse_y > g\ry-g\height*(i+.25) + yOffset
							EntityColor mesh,255,200,100
							point = i
						EndIf
					Next
					If mh Then
						PlaySound MGui_Click
						If point Then
							MGui_SelectItem(g,point)
							MGui_Event(Handle(c),c\onchange)
						EndIf
						HideEntity c\lmesh
						c\seld = 0
					EndIf
				Else
					If c\lbganim > 0 Then c\lbganim = c\lbganim - 2*main_gspe Else c\lbganim = 0
					ScaleEntity c\lbgmesh,c\lbganim/100.0,1,1
					EntityAlpha c\lbgmesh,c\lbganim/200.0
					If mouse_X>g\rx And mouse_x<g\rx+g\width And mouse_y<g\ry And mouse_y>g\ry-g\height Then
						EntityColor g\mesh,225,225,225
						If Not MouseDown(1) Then PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),-1
						If mh Then
							PlaySound MGui_Click
							If c\lbgmesh Then FreeEntity c\lbgmesh
							n = CountChildren(c\lmesh)
							yOffset# = Util_MinMax(- mouse_gh - (g\ry - n*g\height), 0, g\ry - 1)
							c\lbgmesh = MGui_Quad(c\mesh,g\width,-yOffset,g\width,n*g\height,100,100,100)
							EntityParent c\lbgmesh,c\mesh
							EntityAlpha c\lbgmesh,0
							PositionEntity c\lmesh,0,yOffset,0
							c\Seld = 1
						EndIf
					Else
						EntityColor c\mesh,100,100,100
						PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),0
					EndIf
				EndIf
			ElseIf g\anim < 10
				EntityAlpha c\mesh,1
			EndIf
		Case gtTick
			t.TTick = Object.TTick(g\Handl)
			
			EntityAlpha t\mesh,(1-g\anim/100)*.5
			PositionEntity t\mesh,EntityX(g\mesh),EntityY(g\mesh),-g\anim*2
			
			HideEntity t\mcaption
			If g\anim = 0
				ShowEntity t\mcaption
				If mouse_X>g\rx And mouse_x<g\rx+g\width And mouse_y<g\ry And mouse_y>g\ry-g\height Then
					EntityColor g\mesh,255,255,255
					If mh Then PlaySound MGui_Click : t\set = 1 - t\set : MGui_Event(Handle(t),t\OnClick) : mh=0
				Else				
					EntityColor t\mesh,100,100,100
					If t\set Then EntityColor t\mesh,100,200,255
				EndIf
			ElseIf g\anim < 10
				EntityAlpha b\mesh,1
			EndIf
		Case gtKeySelect
			k.TKeySelect = Object.TKeySelect(g\Handl)
			
			EntityAlpha k\mesh,(1-g\anim/100)*.5
			PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),-g\anim*2
			
			HideEntity k\mcaption
			If g\anim = 0
				ShowEntity k\mcaption
				If k\active
					For i = 1 To 270
						If Inp_KeyHit(i) Then
							If i > 1
								k\key = i
								FreeEntity k\mcaption
								k\mcaption = Txt_Text(g\caption$+": "+Key_KeyName(k\key), MGui_font, k\mesh)
								ScaleMesh k\mcaption,g\height/10.0,g\height/10.0,g\height/10.0
								EntityOrder k\mcaption ,-13
								MoveEntity k\mcaption,g\width/2-MeshWidth(k\mcaption)/2,-g\height/2-MeshHeight(k\mcaption)/2,0
							EndIf
							k\active = 0
							MGui_Event(Handle(k),k\OnChange)
							Exit
						EndIf
					Next
				Else
					If mouse_X>g\rx And mouse_x<g\rx+g\width And mouse_y<g\ry And mouse_y>g\ry-g\height Then
						EntityColor g\mesh,225,225,225
						If mh Then PlaySound MGui_Click : k\active = 1
					Else
						EntityColor k\mesh,100,100,100
					EndIf
				EndIf
			ElseIf g\anim < 10
				EntityAlpha k\mesh,1
			EndIf
		Case gtInput
			ib.TInput = Object.TInput(g\Handl)
			
			EntityAlpha ib\mesh,(1-g\anim/100)*.5
			PositionEntity ib\mesh,EntityX(g\mesh),EntityY(g\mesh),-g\anim*2
			
			HideEntity ib\mcaption
			HideEntity ib\mtext
			If g\anim = 0
				ShowEntity ib\mcaption
				ShowEntity ib\mtext
				If ib\active
					change = 0
					If (key > 31 And key < 127) Or key >191
						If (ib\mode <> imNum And ib\mode <> imNumIP) Or (key>47 And key<58) Or (key=46 And ib\mode=imNumIP)
							change = 1
							ib\txt = ib\txt + Chr(key)
						EndIf
					ElseIf KeyHit(14)
						change = 1
						If Len(ib\txt) > 0
							ib\txt = Left(ib\txt,Len(ib\txt)-1)
						EndIf
					EndIf
					If change
						FreeEntity ib\mtext
						ib\mtext = Txt_Text(ib\txt, MGui_font, ib\mesh)
						ScaleMesh ib\mtext,g\height/10.0,g\height/10.0,g\height/10.0
						EntityOrder ib\mtext ,-13
						MoveEntity ib\mtext,g\width/2-MeshWidth(ib\mtext)/2,-g\height/2-MeshHeight(ib\mtext)/2,0
						PlaySound MGui_KeyHit
						MGUI_Event(Handle(ib),ib\OnChange)
					EndIf
					If mh Then
						If Not (mouse_X>g\rx And mouse_x<g\rx+g\width And mouse_y<g\ry And mouse_y>g\ry-g\height) Then
							If mh Then ib\active = 0
						EndIf	
					EndIf
				Else
					If mouse_X>g\rx And mouse_x<g\rx+g\width And mouse_y<g\ry And mouse_y>g\ry-g\height Then
						EntityColor g\mesh,225,225,225
						If mh Then PlaySound MGui_Click : ib\active = 1
					Else
						EntityColor ib\mesh,100,100,100
					EndIf
				EndIf
			ElseIf g\anim < 10
				EntityAlpha ib\mesh,1
			EndIf
		Case gtList
			l.TList = Object.TList(g\Handl)
			
			EntityAlpha l\mesh,(1-g\anim/100)*.5
			PositionEntity g\mesh,EntityX(g\mesh),EntityY(g\mesh),-g\anim*2
			HideEntity l\tmesh
			
			If g\anim = 0
				ShowEntity l\tmesh
				N = CountChildren(l\tmesh)
				point = 0
				For i = 1 To N
					mesh = GetChild(l\tmesh,i)
					EntityColor mesh,255,255,255
					If mouse_X>g\rx And mouse_x<g\rx+g\width And mouse_y<g\ry-4*i+3 And mouse_y>g\ry-4*i-1
						EntityColor mesh,255,200,100
						point = i
					EndIf
				Next
				If mh Then
					If point>0 Then
						doubleclick = 0
						If point = l\sel And MilliSecs()-l\clicktime<500 Then doubleclick = 1
						l\clicktime = MilliSecs()
						PlaySound MGui_Click
						MGui_SelectItem(g,point)
						MGui_Event(Handle(l),l\onchange)
						If doubleclick Then MGui_Event(Handle(l),l\ondoubleclick)
					EndIf
				EndIf

			ElseIf g\anim < 10
				EntityAlpha l\mesh,1
			EndIf

		End Select
	Next
End Function

Function MGui_NewGadget.TGadget(x,y,width,height,caption$,typ,handl,mesh,parent.TGadget)
	g.TGadGet	= New TGadGet
	g\x			= x
	g\y			= y
	g\width		= width
	g\height	= height
	g\caption	= caption
	g\parent	= parent
	g\mesh		= mesh
	g\typ		= typ
	g\handl		= handl
	g\anim		= 100
	Return g
End Function

Function MGui_CreateWindow.TGadget(x,y,width,height, caption$, parent.TGadget)
	w.TWindow	= New TWindow
	
	caption = Lang_Translate(caption)
	
	If parent <> Null Then
		w\mbg = MGui_Quad(parent\mesh,x,y,width,height)
	Else
		w\mbg = MGui_Quad(MGui_Piv,x,y,width,height)
	EndIf
	EntityOrder w\mbg,-9
	
	;MGui_NewGadget(x,y,width,height,caption$,gtWindow,Handle(w),w\mbg,parent.TGadget)
	
	Return MGui_NewGadget(x,y,width,height,caption$,gtWindow,Handle(w),w\mbg,parent.TGadget)
End Function

Function MGui_CreateButton.TGadget(x,y,width,height, caption$, r,g,bl, parent.TGadget ,onclick$,hotkey=0)
	b.TButton	= New TButton
	
	caption = Lang_Translate(caption)
	
	b\r			= r
	b\g			= g
	b\b			= bl

	b\onclick	= onclick
	b\hotkey	= hotkey

	If parent <> Null Then
		b\mesh = MGui_Quad(parent\mesh,x,y,width,height,b\r,b\g,b\b)
	Else
		b\mesh = MGui_Quad(MGui_Piv,x,y,width,height,b\r,b\g,b\b)
	EndIf
	
	b\mcaption = Txt_Text(caption$, MGui_font, b\mesh)
	ScaleMesh b\mcaption,height/10.0,height/10.0,height/10.0
	EntityOrder b\mcaption ,-11
	MoveEntity b\mcaption,width/2-MeshWidth(b\mcaption)/2,-height/2-MeshHeight(b\mcaption)/2,0
	
	Return MGui_NewGadget(x,y,width,height,caption$,gtButton,Handle(b),b\mesh,parent.TGadget)
End Function

Function MGui_CreateSlider.TGadget(x,y,width,height, caption$, pos, steps, parent.TGadget ,OnChange$)
	s.TSlider	= New TSlider
		
	s\pos		= pos
	
	s\onChange	= onChange
	
	caption 	= Lang_Translate(caption)
	s\steps		= steps
	
	If parent <> Null Then
		s\mesh = MGui_Quad(parent\mesh,x,y,width,height,20,20,20)
	Else
		s\mesh = MGui_Quad(MGui_Piv,x,y,width,height,20,20,20)
	EndIf
	EntityOrder s\mesh,-12
	
	s\mcaption = Txt_Text(caption$, MGui_font, s\mesh)
	ScaleMesh s\mcaption,height/4.0,height/4.0,height/4.0
	EntityOrder s\mcaption ,-15
	MoveEntity s\mcaption,4,0,0
	
	s\mslider = MGui_Quad(s\mesh,0,0,Float(width)/Float(steps),height+2,150,150,150)
	PositionEntity s\mslider,pos*width/(steps+1),1,0
	EntityOrder s\mslider ,-14
	
	Return MGui_NewGadget(x,y,width,height,caption$,gtSlider,Handle(s),s\mesh,parent)
End Function

Function MGui_GadgetColor(g.TGadget, r,gr,bl)
	b.TButton = Object.TButton(g\handl)
	If b <> Null
		b\r			= r
		b\g			= gr
		b\b			= bl
	Else
		EntityColor g\mesh, r,gr,bl
	EndIf
	
End Function

Function MGui_SetHint(g.TGadget, en$, de$)
	If main_lang = "de_" Then
		g\hint = de
	Else
		g\hint = en
	EndIf

End Function

Function MGui_CreateCombo.TGadget(x,y,width,height, caption$, parent.TGadget ,onChange$)
	c.TCombo	= New TCombo
	
	caption = Lang_Translate(caption)
	
	c\onchange	= onchange

	If parent <> Null Then
		c\mesh = MGui_Quad(parent\mesh,x,y,width,height,100,100,100)
	Else
		c\mesh = MGui_Quad(MGui_Piv,x,y,width,height,100,100,100)
	EndIf
	
	c\lmesh = CreatePivot(c\mesh)
	c\lbgmesh = CreateMesh(c\mesh)
	
	c\smesh = Txt_Text(caption$, MGui_font, c\mesh)
	ScaleMesh c\smesh,height/10.0,height/10.0,height/10.0
	EntityOrder c\smesh ,-11
	MoveEntity c\smesh,width/2-MeshWidth(c\smesh)/2,-height/2-MeshHeight(c\smesh)/2,0
	
	Return MGui_NewGadget(x,y,width,height,caption$,gtCombo,Handle(c),c\mesh,parent.TGadget)
End Function

Function MGui_AddItem(g.TGadget,caption$)
	caption = Lang_Translate(caption)
	Select g\typ
	Case gtCombo
		c.TCombo = Object.TCombo(g\handl)
		n = CountChildren(c\lmesh)
		mesh = Txt_Text(caption$, MGui_font, c\lmesh)
		ScaleMesh mesh,g\height/10.0,g\height/10.0,g\height/10.0
		EntityOrder mesh ,-12
		NameEntity mesh,caption$
		MoveEntity mesh,g\width*1.5-MeshWidth(mesh)/2,-g\height/2-g\height*n-MeshHeight(mesh)/2,0
		HideEntity c\lmesh
	Case gtList
		l.TList = Object.TList(g\handl)
		n = CountChildren(l\tmesh)
		mesh = Txt_Text(caption$, MGui_font, l\tmesh)
		ScaleMesh mesh,.6,.6,.6
		EntityOrder mesh ,-12
		NameEntity mesh,caption$
		MoveEntity mesh,1,-5-4*n,0
	End Select
	Return n+1
End Function

Function MGui_SelectItem(g.TGadget,id)
	Select g\typ
	Case gtCombo
		c.TCombo	= Object.TCombo(g\handl)
		mesh		= GetChild(c\lmesh,id)
		c\sel		= id
		c\seltext 	= EntityName(mesh)
		
		FreeEntity c\smesh
		c\smesh = Txt_Text(Util_Shorten$(g\caption+": "+c\seltext,g\width*.45), MGui_font, c\mesh)
		ScaleMesh c\smesh,g\height/10.0,g\height/10.0,g\height/10.0
		EntityOrder c\smesh ,-12
		MoveEntity c\smesh,g\width/2-MeshWidth(c\smesh)/2,-g\height/2-MeshHeight(c\smesh)/2,0
	Case gtList
		l.TList = Object.TList(g\handl)
		n = CountChildren(l\tmesh)
		For i = n To 1 Step -1
			mesh = GetChild(l\tmesh,i)
			ScaleEntity mesh,1,1,1
			;PositionEntity mesh,1,-5-4*i+4,0
		Next
		mesh	= GetChild(l\tmesh,id)
		l\sel	= id
		l\seltext=EntityName(mesh)
		ScaleEntity mesh,1,1.3,1
		;MoveEntity mesh,0,0,-2
	End Select
End Function

Function MGui_SetItemCaption(g.TGadget,id,caption$)
	caption = Lang_Translate(caption)
	Select g\typ
	Case gtCombo

	Case gtList
		l.TList = Object.TList(g\handl)
		mesh	= GetChild(l\tmesh,id)
		mesh = Txt_TextToMesh(caption$, MGui_font, mesh)
		ScaleMesh mesh,.6,.6,.6
		EntityOrder mesh ,-12
		NameEntity mesh,caption$
	End Select
	Return CountChildren(l\tmesh)
End Function

Function MGui_SetItemAlpha(g.TGadget,id,alpha#)
	caption = Lang_Translate(caption)
	Select g\typ
	Case gtCombo

	Case gtList
		l.TList = Object.TList(g\handl)
		mesh	= GetChild(l\tmesh,id)
		EntityAlpha mesh, alpha#
	End Select
	Return n+1
End Function

Function MGui_RemoveItems(g.TGadget)
	Select g\typ
	Case gtCombo
		c.TCombo = Object.TCombo(g\handl)
		n = CountChildren(c\lmesh)
		For i = n To 1 Step -1
			mesh = GetChild(c\lmesh,i)
			FreeEntity mesh
		Next
	Case gtList
		l.TList = Object.TList(g\handl)
		n = CountChildren(l\tmesh)
		For i = n To 1 Step -1
			mesh = GetChild(l\tmesh,i)
			FreeEntity mesh
		Next
	End Select
End Function

Function MGui_SetActive(g.TGadget,state)
	Select g\typ
	Case gtInput
		in.TInput	= Object.TInput(g\handl)
		in\active	= state
		If state Then
			EntityColor in\mesh,225,225,225
		Else
			EntityColor in\mesh,100,100,100
		End If
	End Select
End Function

Function MGui_CreateTick.TGadget(x,y,width,height, caption$, set, parent.TGadget ,onclick$)
	t.TTick		= New TTick
	
	t\set		= set
	caption		= Lang_Translate(caption)

	t\onclick	= onclick
	
	If parent <> Null Then
		t\mesh = MGui_Quad(parent\mesh,x,y,width,height,100,100,100)
	Else
		t\mesh = MGui_Quad(MGui_Piv,x,y,width,height,100,100,100)
	EndIf
	EntityOrder t\mesh,-12
	
	If set Then EntityColor t\mesh,100,200,255
	
	t\mcaption = Txt_Text(caption$, MGui_font, t\mesh)
	ScaleMesh t\mcaption,height/10.0,height/10.0,height/10.0
	EntityOrder t\mcaption ,-13
	MoveEntity t\mcaption,width+2,-height/2-MeshHeight(t\mcaption)/2,0
	;-MeshWidth(b\mcaption),MeshHeight(b\mcaption),0
	
	Return MGui_NewGadget(x,y,width,height,caption$,gtTick,Handle(t),t\mesh,parent.TGadget)
End Function

Function MGui_CreateKeySelect.TGadget(x,y,width,height, caption$, key, parent.TGadget ,onchange$)
	k.TKeySelect = New TKeySelect
	
	caption 	= Lang_Translate(caption)
	k\key		= key

	k\onchange	= onchange

	If parent <> Null Then
		k\mesh = MGui_Quad(parent\mesh,x,y,width,height,100,100,100)
	Else
		k\mesh = MGui_Quad(MGui_Piv,x,y,width,height,100,100,100)
	EndIf
	EntityOrder k\mesh,-12
	
	k\mcaption = Txt_Text(caption$+": "+Key_KeyName(key), MGui_font, k\mesh)
	ScaleMesh k\mcaption,height/10.0,height/10.0,height/10.0
	EntityOrder k\mcaption ,-13
	MoveEntity k\mcaption,width/2-MeshWidth(k\mcaption)/2,-height/2-MeshHeight(k\mcaption)/2,0
	
	Return MGui_NewGadget(x,y,width,height,caption$,gtKeySelect,Handle(k),k\mesh,parent.TGadget)
End Function

Function MGui_SelectKey(g.TGadget,key)
	k.TKeySelect = Object.TKeySelect(g\handl)
	If k <> Null
		k\key = key
		FreeEntity k\mcaption
		k\mcaption = Txt_Text(g\caption$+": "+Key_KeyName(k\key), MGui_font, k\mesh)
		ScaleMesh k\mcaption,g\height/10.0,g\height/10.0,g\height/10.0
		EntityOrder k\mcaption ,-13
		MoveEntity k\mcaption,g\width/2-MeshWidth(k\mcaption)/2,-g\height/2-MeshHeight(k\mcaption)/2,0
	EndIf
End Function

Function MGui_CreateInput.TGadget(x,y,width,height, caption$, txt$, parent.TGadget ,onchange$="", mode=imDefault)
	i.TInput = New TInput
	
	caption 	= Lang_Translate(caption)
	i\txt		= txt
	i\mode		= mode

	i\onchange	= onchange

	If parent <> Null Then
		i\mesh = MGui_Quad(parent\mesh,x,y,width,height,100,100,100)
	Else
		i\mesh = MGui_Quad(MGui_Piv,x,y,width,height,100,100,100)
	EndIf
	EntityOrder i\mesh,-12
	
	i\mcaption = Txt_Text(caption$, MGui_font, i\mesh)
	ScaleMesh i\mcaption,height/10.0,height/10.0,height/10.0
	EntityOrder i\mcaption ,-13
	MoveEntity i\mcaption,4,0,0
	
	i\mtext = Txt_Text(txt, MGui_font, i\mesh)
	ScaleMesh i\mtext,height/10.0,height/10.0,height/10.0
	EntityOrder i\mtext ,-13
	MoveEntity i\mtext,width/2-MeshWidth(i\mtext)/2,-height/2-MeshHeight(i\mtext)/2,0
	
	Return MGui_NewGadget(x,y,width,height,caption$,gtInput,Handle(i),i\mesh,parent.TGadget)
End Function

Function MGui_ChangeText(g.TGadget,txt$, feedback=1)
	ib.TInput = Object.TInput(g\handl)
	ib\txt = txt
	FreeEntity ib\mtext
	ib\mtext = Txt_Text(ib\txt, MGui_font, ib\mesh)
	ScaleMesh ib\mtext,g\height/10.0,g\height/10.0,g\height/10.0
	EntityOrder ib\mtext ,-13
	MoveEntity ib\mtext,g\width/2-MeshWidth(ib\mtext)/2,-g\height/2-MeshHeight(ib\mtext)/2,0
	If feedback Then MGUI_Event(Handle(ib),ib\OnChange)
End Function

Function MGui_CreateList.TGadget(x,y,width,height,parent.TGadget ,OnChange$, OnDoubleClick$)
	l.TList = New TList
	
	l\onchange		= onchange
	l\ondoubleclick	= ondoubleclick
	
	If parent <> Null Then
		l\mesh = MGui_Quad(parent\mesh,x,y,width,height,100,100,100)
	Else
		l\mesh = MGui_Quad(MGui_Piv,x,y,width,height,100,100,100)
	EndIf
	l\tmesh = CreatePivot(l\mesh)
	
	Return MGui_NewGadget(x,y,width,height,caption$,gtList,Handle(l),l\mesh,parent.TGadget)
End Function

Function MGui_Quad(parent,x#=-1,y#=-1,w#=2,h#=2,r=155,g=155,b=155)
	;If mgui_mbgtex=0 Then mgui_mbgtex= LoadTexture(MGUI_BGtex_name$,2)
	mesh		= CreateMesh(parent)
	sur			= CreateSurface(mesh)
	
	;If x< 0 Then x=0
	;If y< 0 Then y=0
	If parent = MGui_Piv Then
		x=x-100
		y=-y+100*main_ratio
	Else
		y=-y
	EndIf

	Local vert[3]
	vert[0]	= AddVertex(sur,	0,-h,0,		0,0)
	vert[1]	= AddVertex(sur,	w,-h,0,		1,0)
	vert[2]	= AddVertex(sur,	0,0,0,			0,1)
	vert[3]	= AddVertex(sur,	w,0,0,		1,1)
	
	AddTriangle sur, vert[0], vert[2], vert[1]
	AddTriangle sur, vert[1], vert[2], vert[3]
	
	EntityFX mesh,1+8+16
	EntityOrder mesh,-10
	EntityColor mesh,r,g,b
	EntityAlpha mesh,.5
	;DebugLog Float(x+EntityX(parent))+" y "+Float(y+EntityY(parent))+" z "+Float(EntityZ(MGui_Piv))

	PositionEntity mesh,x,y,0
	;PositionEntity mesh, x+EntityX(parent),y+EntityY(parent),EntityZ(MGui_Piv),1
;	PositionEntity mesh, x,y,EntityZ(MGui_Piv),1
	

	;EntityTexture mesh,mgui_mbgtex
	Return mesh
End Function

Function MGui_Pos(mesh,x#,y#)
	PositionEntity mesh,x,y,0
End Function

Function MGUI_size(G.TGadget,width#,height#)
	Select g\typ
		Case gtWindow
			Local w.Twindow =Object.Twindow(g\Handl) 
			ScaleMesh(w\mbg,width/g\width,height/g\height,1)
			g\width=width
			g\height=height
		
		Case gtButton
			
		Case gtSlider

	End Select
End Function


Function MGUI_Clear()
	MGui_ClearHoverInfo()
	Delete Each TGadget
	Delete Each TWindow
	Delete Each TButton
	Delete Each TSlider
	Delete Each TCombo
	Delete Each TTick
	Delete Each TKeySelect
	Delete Each TInput
	Delete Each TList
End Function







Type MGUI_hoverinfo
	Field txt$
	Field bg, tmesh
	Field fade#
	Field timer
End Type

Function MGUI_CreateHoverInfo.MGUI_hoverinfo(x#=0,y#=0, txt$="")
	h.MGUI_hoverinfo = New MGUI_hoverinfo
	
	h\bg = CreateSprite(MGui_Piv)
	HandleSprite h\bg,-1,1
	EntityColor h\bg,0,0,0
	EntityOrder h\bg,-20
	h\tmesh = CreateMesh(h\bg)
	
	MGUI_SetHoverInfo(h, x,y, txt)
	Return h
End Function

Function MGUI_SetHoverInfo(h.MGUI_hoverinfo, x#,y#, txt$)
	If txt <> "" And h\txt <> txt
		Local width = Util_MinMax(main_width, 1024,2000)
		Local height = main_height * width / main_width
		Local hwidth = width / 2, hheight = height/2
		
		FreeEntity h\tmesh
		h\tmesh = Txt_Text(txt$, MGui_font, h\bg,0,150)
		ScaleEntity h\tmesh,.4,.4,.4
		EntityOrder h\tmesh,-21
		wid# = MeshWidth(h\tmesh)
		hei# = MeshHeight(h\tmesh)
		ScaleSprite h\bg,wid*.2,hei*.2
		MoveEntity h\tmesh,0,-4,0
		PositionEntity h\bg,x,y,0
		If Left(h\txt,5) <> Left(txt,5) h\fade = -.5
	EndIf
	h\timer = 1000
	h\txt = txt
End Function

Function MGUI_UpdateHoverInfo()
	For h.MGUI_hoverinfo = Each MGUI_hoverinfo
		EntityAlpha h\bg, h\fade*.8
		EntityAlpha h\tmesh, h\fade
		If h\txt = ""
			If h\fade > 0 Then h\fade = h\fade - .04 * main_gspe
		Else
			If h\fade < 1 Then h\fade = h\fade + .04 * main_gspe
			h\timer = h\timer - main_mscleft
			If h\timer < 0 Then h\txt=""
		EndIf
	Next
End Function

Function MGUI_RemoveHoverInfo(h.MGui_hoverinfo)
	FreeEntity bg
	Delete h
End Function

Function MGUI_ClearHoverInfo()
	For h.MGui_hoverinfo = Each MGui_hoverinfo
		MGui_RemoveHoverInfo(h)
	Next
End Function