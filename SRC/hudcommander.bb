Global hud_compiv

Global hud_selrect
Global hud_hover
Global hud_commandermode

Global hud_comupdate=0

Global hud_moveto
Global hud_moveto2
Global hud_movetopiv
Global hud_movetomode

Global hud_cbg

Global hud_comhoverinfo.hud_hoverinfo

Type hud_selship
	Field s.ship
	Field selected
	Field selsprite
	Field bg
	Field icon
	Field hp,shield
	Field tar
End Type

Function Hud_InitCommander()
	hud_compiv = CreatePivot(cc_cam)
	hud_selrect = CreateSprite(hud_compiv)
	
	hud_cbg = LoadSprite(gfxd+"GUI/combg.png",1+2)
	ScaleSprite hud_cbg,20,20
	EntityAlpha hud_cbg,.5
	HideEntity hud_cbg
	
	EntityColor hud_selrect,0,0,0
	HandleSprite hud_selrect,-1,1
	EntityAlpha hud_selrect,.5
	EntityOrder hud_selrect, -8
	EntityFX hud_selrect, 1+8+16
	HideEntity hud_selrect
	
	hud_moveto = CopyEntity(shi_aura2, hud_compiv)
	ScaleSprite hud_moveto, 20,20
	HideEntity hud_moveto
	hud_moveto2 = CreateCube(hud_moveto)
	PositionMesh hud_moveto2,0,-1,0
	EntityFX hud_moveto2,1+8+16
	HideEntity hud_moveto2
	
	hud_movetopiv = CreatePivot()
	
	hud_hover = CopyEntity(shi_aura1, hud_compiv)
	EntityOrder hud_hover,-5
	HideEntity hud_hover
	
	hud_commandermode = 0
	
	hud_comhoverinfo = Hud_CreateHoverInfo()
End Function

Function Hud_UpdateCommander()
	If hud_commandermode = 0 Or hud_comupdate=1
		Hud_CResetShips()
	EndIf
	hud_comupdate = 0
	ShowEntity hud_compiv
	If hud_commandermode=0 Then hud_movetomode = 0
	hud_commandermode = 1
	If Select_ByRect=1 Then 
		x = byrectx + byrectw*(byrectw < 0)
		y = byrecty + byrecth*(byrecth < 0)
		PositionEntity hud_selrect, -main_hwidth+x,main_hheight-y,main_hwidth
		ScaleSprite hud_selrect,Abs(byrectw/2.0), Abs(byrecth/2.0)
		ShowEntity hud_selrect
	ElseIf Select_byRect = 2
		x = byrectx + byrectw*(byrectw < 0)
		y = byrecty + byrecth*(byrecth < 0)
		
		If Not (Inp_KeyDown(42) Or Inp_KeyDown(54)) Then
			For s.hud_selship = Each hud_selship
				s\selected = 0
			Next
		EndIf
		
		For s.hud_selship = Each hud_selship
			If RectsOverlap((EntityX(s\bg,0)+512)*main_hwidth/512-15,(-EntityY(s\bg,0)+hud_height)*main_hheight/hud_height-15,30,30, x,y, Abs(byrectw), Abs(byrecth)) Then
				s\selected = 1
			ElseIf s\s\spawntimer <= 0
				CameraProject(cc_cam, EntityX(s\s\piv,1),EntityY(s\s\piv,1),EntityZ(s\s\piv,1))
				If ProjectedZ() > 0 Then
					If x < ProjectedX() And x+Abs(byrectw) > ProjectedX() And y < ProjectedY() And y+Abs(byrecth) > ProjectedY() Then
						s\selected = 1
					EndIf
				EndIf
			EndIf
		Next
	Else
		HideEntity hud_selrect
	EndIf
	
	PositionEntity hud_hover, 50000,50000,50000
	HideEntity hud_hover
	For sh.ship = Each ship
		If sh\spawntimer <= 0 Then
			CameraProject(cc_cam, EntityX(sh\piv,1),EntityY(sh\piv,1),EntityZ(sh\piv,1))
			If ProjectedZ() > 0 Then
				d# = EntityDistance(sh\piv, cc_cam)
				If (mx-ProjectedX())^2+(my-ProjectedY())^2 < (sh\shc\size+10) * (sh\shc\size + 10) * 5000 / d Then
					If d#<EntityDistance(hud_hover, cc_cam) Then
						PositionEntity hud_hover, EntityX(sh\piv,1),EntityY(sh\piv,1),EntityZ(sh\piv,1),1
						tship.ship = sh
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	tflag.flag = Null
	For f.flag = Each flag
		CameraProject(cc_cam, EntityX(f\mesh,1),EntityY(f\mesh,1),EntityZ(f\mesh,1))
		If ProjectedZ() > 0 Then
			d# = EntityDistance(f\mesh, cc_cam)
			If (mx-ProjectedX())^2+(my-ProjectedY())^2 < 200000 / d Then
				PositionEntity hud_hover, EntityX(f\mesh,1),EntityY(f\mesh,1),EntityZ(f\mesh,1),1
				tflag = f
			EndIf
		EndIf
	Next
	If tflag <> Null Then
		EntityColor hud_hover, 255,255*(tflag\team=main_pl\team),255*(tflag\team=main_pl\team)
		ScaleSprite hud_hover, 150,150
		Hud_SetHoverInfo(hud_comhoverinfo, mx, my,tflag\name)
		If mrh = 1 Then
			ttxt$ = ""
			For s.hud_selship = Each hud_selship
				If s\selected Then
					If ttxt <> "" Then ttxt = ttxt + ","
					ttxt = ttxt + s\s\id
				EndIf
			Next
			If ttxt <> "" Then CC_Message("Command: MoveTo "+ttxt+" "+EntityX(tflag\mesh)+","+EntityY(tflag\mesh)+","+EntityZ(tflag\mesh))
		EndIf
		ShowEntity hud_hover
	ElseIf tship <> Null Then
		EntityColor hud_hover, 255,255*(tship\team=main_pl\team),255*(tship\team=main_pl\team)
		ScaleSprite hud_hover, 100+4*tship\shc\size,100+4*tship\shc\size
		Local info$ = tship\name+Chr(13)
		If tship\hitpoints > 0 Then
			info = info + "H:"+Chr(9)+"1[#progr{"+Int(tship\hitpoints * 100.0 / Float(tship\shc\hitpoints))+"}]" + Chr(13)
		Else 
			info = info + "H:"+Chr(9)+"1 down" + Chr(13)
		EndIf
		If tship\shields > 0 Then
			info = info + "S:"+Chr(9)+"1[#progr{"+Int(tship\shields * 100.0 / Float(tship\shc\shields))+"}]"
		Else
			info = info + "S:"+Chr(9)+"1 down" + Chr(13)
		EndIf
		Hud_SetHoverInfo(hud_comhoverinfo, mx, my, info)
		If mh = 1 Or Select_byRect = 2 Then
			t = Not (Inp_KeyDown(42) Or Inp_KeyDown(54)) 
			For s.hud_selship = Each hud_selship
				If t Then s\selected = 0
				If s\s = tship Then s\selected = 1
			Next
		ElseIf mrh=1
			If tship\team = main_pl\team
				
			Else
				ttxt$ = ""
				For s.hud_selship = Each hud_selship
					If s\selected Then
						If ttxt <> "" Then ttxt = ttxt + ","
						ttxt = ttxt + s\s\id
					EndIf
				Next
				If ttxt <> "" Then CC_Message("Command: Attack "+ttxt+" "+tship\id)
			EndIf
		EndIf
		ShowEntity hud_hover
	ElseIf mrh=1
		EntityPickMode cc_gridplane2,2
		If CameraPick(cc_cam, mx, my)
			PositionEntity hud_movetopiv,PickedX(),PickedY(),PickedZ()
			
			hud_movetomode = 1
		EndIf
		EntityPickMode cc_gridplane2,0
	ElseIf mrh=2 And hud_movetomode=1
		
	ElseIf hud_movetomode
		For s.hud_selship = Each hud_selship
			If s\selected Then
				If ttxt <> "" Then ttxt = ttxt + ","
				ttxt = ttxt + s\s\id
			EndIf
		Next
		If ttxt <> "" Then CC_Message("Command: MoveTo "+ttxt+" "+EntityX(hud_movetopiv)+","+EntityY(hud_movetopiv)+","+EntityZ(hud_movetopiv))
		hud_movetomode = 0
	EndIf
	
	If hud_movetomode Then 
		ShowEntity hud_moveto
		ShowEntity hud_moveto2
		
		TFormPoint 0,0,0,hud_movetopiv, hud_compiv
		xf# = TFormedX()
		yf# = TFormedY()
		zf# = TFormedZ()
		PositionEntity hud_moveto,512*xf/zf, 512*yf/zf, 512
		;TFormPoint EntityX(hud_movetopiv),0,EntityZ(hud_movetopiv),0,hud_compiv
		;zf2# = TFormedZ()
		;x1# = 512*TFormedX()/zf2-512*xf/zf
		;y1# = 512*TFormedY()/zf2-512*yf/zf
		AlignToVector hud_moveto2,0,1,0,2
		ScaleEntity hud_moveto2,1,(EntityY(hud_movetopiv)-EntityY(cc_gridplane))*512/zf/2,1
		If mrh = 2 Then
			TranslateEntity hud_movetopiv,0,-mys*zf/512,0;<----
		EndIf
	Else
		HideEntity hud_moveto
	EndIf
	
	If Inp_KeyHit(cc_stopcommand)
		ttxt$ = ""
		For s.hud_selship = Each hud_selship
			If s\selected Then
				If ttxt <> "" Then ttxt = ttxt + ","
				ttxt = ttxt + s\s\id
			EndIf
		Next
		If ttxt <> "" Then CC_Message("Command: Stop "+ttxt)
	EndIf
	
	For s.hud_selship = Each hud_selship
		If s\s\human
			Select s\s\order
			Case ORDER_ATTACK
				
			Case ORDER_MOVETO
				If EntityDistance(s\s\piv, s\s\opiv) < 300 Then
					s\s\order = 0
					s\s\opiv = 0
				EndIf
			Default
				HideEntity hud_ctshow
				HideEntity hud_ctarget
			End Select 
		EndIf
		If s\s\spawntimer > 0 Then 
			EntityAlpha s\bg,.15
			EntityAlpha s\icon,.5
			HideEntity s\hp
			HideEntity s\shield
			HideEntity s\selsprite
			HideEntity s\tar
			If s\selected Then
				EntityAlpha s\bg,.4
			EndIf
		Else
			ShowEntity s\hp
			ShowEntity s\shield
			EntityAlpha s\bg,.5
			HideEntity s\selsprite
			If s\selected Then
				EntityAlpha s\bg,1
				ShowEntity s\selsprite
				PositionEntity s\selsprite, s\s\x, s\s\y, s\s\z, 1
			EndIf
			
			Select s\s\order
			Case ORDER_ATTACK
				PositionEntity s\tar, s\s\x, s\s\y, s\s\z, 1
				PointEntity s\tar,s\s\oship\piv
				scale# = EntityDistance(s\tar,cc_cam)/1024.0
				ScaleEntity s\tar,scale,scale,EntityDistance(s\tar, s\s\oship\piv)/2
				EntityColor s\tar,255,0,0
				ShowEntity s\tar
			Case ORDER_MOVETO
				PositionEntity s\tar, s\s\x, s\s\y, s\s\z, 1
				PointEntity s\tar,s\s\opiv
				scale# = EntityDistance(s\tar,cc_cam)/1024.0
				ScaleEntity s\tar,scale,scale,EntityDistance(s\tar, s\s\opiv)/2
				EntityColor s\tar,255,255,255
				ShowEntity s\tar
			Default
				HideEntity s\tar
			End Select 
			EntityAlpha s\icon,1
			ScaleSprite s\hp, 10.0 * s\s\hitpoints / Float(s\s\shc\hitpoints), 1
			ScaleSprite s\shield, 10.0 * s\s\shields / Float(s\s\shc\shields), 1
		EndIf
	Next
End Function

Function Hud_ClearCommander()
	For s.hud_selship = Each hud_selship
		FreeEntity s\selsprite
		FreeEntity s\bg
		FreeEntity s\tar
		Delete s
	Next
	hud_commandermode = 0
	FreeEntity hud_sbg
	FreeEntity hud_movetopiv
End Function

Function Hud_CResetShips()
	For s.hud_selship = Each hud_selship
		FreeEntity s\bg
		FreeEntity s\selsprite
		FreeEntity s\tar
		Delete s
	Next
	If main_pl = Null Then Return 0
	For s2.ship = Each ship
		If s2\team = main_pl\team Then
			s = New hud_selship
			s\s = s2
			s\bg = CopyEntity(hud_cbg, hud_compiv)
			EntityOrder s\bg,-6
			s\icon = CopyEntity(s2\shc\mini, s\bg)
			ScaleEntity s\icon,3.5,3.5,1
			EntityOrder s\icon,-7
			
			s\selsprite = CopyEntity(shi_aura1, hud_compiv)
			EntityOrder s\selsprite, -5
			ScaleSprite s\selsprite, 100+4*s\s\shc\size, 100+4*s\s\shc\size
			
			s\hp  = CreateSprite(s\bg)
			HandleSprite s\hp,-1,1
			PositionEntity s\hp,-10,11,0
			EntityOrder s\hp,-8
			EntityColor s\hp,250,10,0
			
			s\shield = CopyEntity(s\hp,s\bg)
			PositionEntity s\shield,-10,9,0
			EntityColor s\shield,10,40,250
			
			s\tar = CreateCube(hud_compiv)
			EntityFX s\tar,1+8
			PositionMesh s\tar,0,0,1
			HideEntity s\tar
		EndIf
	Next
	
	Hud_CSortShips()
End Function

Function HUD_CAddShip(s2.ship)
	If s2\team = main_pl\team Then
		s.hud_selship = New hud_selship
		s\s = s2
		s\bg = CopyEntity(hud_cbg, hud_compiv)
		EntityOrder s\bg,-6
		s\icon = CopyEntity(s2\shc\mini, s\bg)
		ScaleEntity s\icon,3.5,3.5,1
		EntityOrder s\icon,-7
		
		s\selsprite = CopyEntity(shi_aura1, hud_compiv)
		EntityOrder s\selsprite, -5
		ScaleSprite s\selsprite, 100+4*s\s\shc\size, 100+4*s\s\shc\size
		
		s\hp  = CreateSprite(s\bg)
		HandleSprite s\hp,-1,1
		PositionEntity s\hp,-10,11,0
		EntityOrder s\hp,-8
		EntityColor s\hp,250,10,0
		
		s\shield = CopyEntity(s\hp,s\bg)
		PositionEntity s\shield,-10,9,0
		EntityColor s\shield,10,40,250
		
		s\tar = CreateCube(hud_compiv)
		PositionMesh s\tar,0,0,1
		EntityFX s\tar,1+8
		HideEntity s\tar
	
		Hud_CSortShips()
	EndIf
End Function

Function Hud_CSortShips()
	row# = 0
	For i1 = 2 To 1 Step -1
		For i2 = 1 To 5
			c = 0
			col# = 0
			For s.hud_selship = Each hud_selship
				If s\s\class = i2 And s\s\typ = i1 Then
					PositionEntity s\bg, 440-col*35,120-row*35,512
					c = c + 1
					col = col + 1
					If col >= 4 Then row = row + 1 : col = 0
				EndIf
			Next
			If col > 0 Then row = row + 1.4 
		Next
	Next
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D