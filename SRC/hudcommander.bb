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
	Field size
	Field selected
	Field selsprite
	Field bg
	Field icon
	Field hp,shield
	Field tar
End Type

Function Hud_InitCommander()
	hud_compiv = CreatePivot(hud_cam_piv)
	hud_selrect = CreateSprite(hud_compiv)
	
	hud_cbg = Util_LoadSprite(gfxd+"GUI/combg.png",1+2)
	ScaleMesh hud_cbg,20,20,1
	PositionMesh hud_cbg,-10,0,0
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
		PositionEntity hud_selrect, -main_hwidth+x,main_hheight-y,main_hwidth/cc_cam_realzoom
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
			If RectsOverlap((EntityX(s\bg,0)+512-30*s\size)*main_hwidth/512,(-EntityY(s\bg,0)+hud_height-15*s\size)*main_hheight/hud_height, 30*s\size*main_hwidth/512, 30*s\size*main_hwidth/512, x,y, Abs(byrectw), Abs(byrecth)) Then
				s\selected = 1
			ElseIf s\s\spawntimer <= 0
				Shi_Update2dCoords(s\s)
				If s\s\z2d > 0
					If x < s\s\x2d And x+Abs(byrectw) > s\s\x2d And y < s\s\y2d And y+Abs(byrecth) > s\s\y2d Then
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
	Local zDist = 100000.0
	For sh.ship = Each ship
		If sh\spawntimer <= 0 Then
			Shi_Update2dCoords(sh)
			If sh\z2d > 0 Then
				If (mx-sh\x2d)^2+(my-sh\y2d)^2 < (sh\shc\size+10) * (sh\shc\size + 10) * 5000 / sh\z2d Then
					If sh\z2d < zDist Then
						zDist = sh\z2d
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
			info = info + "H:"+Chr(9)+"1 " + Chr(13)
		EndIf
		If tship\shields > 0 Then
			info = info + "S:"+Chr(9)+"1[#progr{"+Int(tship\shields * 100.0 / Float(tship\shc\shields))+"}]"
		Else
			info = info + "S:"+Chr(9)+"1 " + Chr(13)
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
				scale# = EntityDistance(s\tar,hud_cam_piv)/1024.0
				ScaleEntity s\tar,scale,scale,EntityDistance(s\tar, s\s\oship\piv)/2
				EntityColor s\tar,255,0,0
				ShowEntity s\tar
			Case ORDER_MOVETO
				PositionEntity s\tar, s\s\x, s\s\y, s\s\z, 1
				PointEntity s\tar,s\s\opiv
				scale# = EntityDistance(s\tar,hud_cam_piv)/1024.0
				ScaleEntity s\tar,scale,scale,EntityDistance(s\tar, s\s\opiv)/2
				EntityColor s\tar,255,255,255
				ShowEntity s\tar
			Default
				HideEntity s\tar
			End Select 
			EntityAlpha s\icon,1
			ScaleSprite s\hp, s\size * 10.0 * s\s\hitpoints / Float(s\s\shc\hitpoints), 1.2 * s\size
			ScaleSprite s\shield, s\size * 10.0 * s\s\shields / Float(s\s\shc\shields), 1.2 * s\size
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
			HUD_CRefreshShipSel(s)
		EndIf
	Next
	
	Hud_CSortShips()
End Function

Function Hud_CRefreshShips()
	For s.hud_selship = Each hud_selship
		HUD_CRefreshShipSel(s)
	Next
End Function

Function HUD_CAddShip(s2.ship)
	If s2\team = main_pl\team Then
		s.hud_selship = New hud_selship
		s\s = s2
		HUD_CRefreshShipSel(s)
	
		Hud_CSortShips()
	EndIf
End Function

Function HUD_CRefreshShip(s2.ship)
 	If main_pl <> Null Then
		If s2\team <> main_pl\team Then
			HUD_CDeleteShip(s2)
			Return
		EndIf
 	EndIf
	
	For s.hud_selship = Each hud_selship
		If s\s = s2 Then
			HUD_CRefreshShipSel(s)
		EndIf
	Next
	Hud_CSortShips()
End Function

Function HUD_CRefreshShipSel(s.hud_selship)
	s2.ship = s\s
	
	If s\bg <> 0 Then FreeEntity s\bg
	If s\selsprite <> 0 Then FreeEntity s\selsprite
	If s\tar <> 0 Then FreeEntity s\tar
	
	s\size = s2\shc\mmapsize
	If s\size > 3 Then s\size = 3
	
	s\bg = CopyEntity(hud_cbg, hud_compiv)
	ScaleEntity s\bg,s\size,s\size,1
	EntityOrder s\bg,-16
	s\icon = CopyEntity(s2\shc\mini, s\bg)
	PositionEntity s\icon,-10,-1,0
	ScaleEntity s\icon,1.4+s\size*1.2,1.4+s\size*1.2,1
	EntityOrder s\icon,-20
	
	s\selsprite = CopyEntity(shi_aura1, hud_compiv)
	EntityOrder s\selsprite, -5
	ScaleSprite s\selsprite, 100+4*s\s\shc\size, 100+4*s\s\shc\size
	
	s\hp  = CreateSprite(s\bg)
	HandleSprite s\hp,-1,1
	PositionEntity s\hp,-20,12,0
	EntityOrder s\hp,-19
	EntityColor s\hp,250,10,0
	
	s\shield = CopyEntity(s\hp,s\bg)
	PositionEntity s\shield,-20,9,0
	EntityColor s\shield,50,105,250
	EntityOrder s\shield,-18
	
	s\tar = CreateCube(hud_compiv)
	PositionMesh s\tar,0,0,1
	EntityFX s\tar,1+8
	HideEntity s\tar
	
	; default display to unselected ship
 	EntityAlpha s\bg,.15
 	EntityAlpha s\icon,.5
 	HideEntity s\hp
 	HideEntity s\shield
 	HideEntity s\selsprite
 	HideEntity s\tar
 	If s\selected Then
 		EntityAlpha s\bg,.4
 	EndIf
End Function

Function HUD_CDeleteShip(s2.ship)
	For s.hud_selship = Each hud_selship
		If s\s = s2 Then
			If s\bg <> 0 Then FreeEntity s\bg
			If s\selsprite <> 0 Then FreeEntity s\selsprite
			If s\tar <> 0 Then FreeEntity s\tar
			Delete s
		EndIf
	Next
	Hud_CSortShips()
End Function

Function Hud_CSortShips()
	row# = 0
	For i1 = 2 To 1 Step -1
		For i2 = 1 To 5
			c = 0
			col# = 0
			rowMaxSize = 0
			For s.hud_selship = Each hud_selship
				If s\s\class = i2 And s\s\typ = i1 Then
					If s\size > rowMaxSize Then rowMaxSize = s\size
					PositionEntity s\bg, 440-col*35,120-row*35,512
					c = c + 1
					col = col + s\size
					If col >= 4 Then row = row + rowMaxSize : col = 0
				EndIf
			Next
			If col > 0 Then	row = row + rowMaxSize
			If c >  0
				row = row + .4
			EndIf
		Next
	Next
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D