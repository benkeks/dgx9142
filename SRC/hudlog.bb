Type logtxt
	Field mesh
	Field group
	Field time
End Type

Type hudinput
	Field active
	Field txt$
	Field mesh,piv,bg
	Field dtimer
	Field pulse#
End Type

Global hud_logpiv
Global hud_logshift

Function HUD_InitLog()
	hud_logpiv = CreatePivot(hud_cam_piv)
	hud_logshift = 0
	PositionEntity hud_logpiv, -340,-350*main_ratio+100,350
End Function

Function HUD_CreateInput.hudinput()
	i.hudinput = New hudinput
	i\active = 0
	i\txt = ""
	i\piv = CreatePivot(hud_cam_piv)
	i\mesh = CreatePivot(i\piv)
	
	i\bg	= CreateSprite(i\piv)
	EntityTexture i\bg, hud_buttontex
	ScaleSprite i\bg, 60,11
	HandleSprite i\bg,-.93,-.8
	EntityOrder i\bg, -14
	EntityAlpha i\bg, .9
	
	Return i
End Function

Function HUD_ActivateInput(i.hudinput)
	i\active = 1
	i\txt = ""
	FreeEntity i\mesh
	i\mesh =  Txt_Text("> "+i\txt+"_", hud_font,i\piv)
	EntityOrder i\mesh,-15
End Function

Function HUD_UpdateInput()
	For i.hudinput = Each hudinput
		change = 0
		If i\active
			i\pulse = (i\pulse + .01*main_gspe) Mod 2
			If i\pulse > 1 Then 
				EntityAlpha i\bg, 2-i\pulse
			Else
				EntityAlpha i\bg, i\pulse
			EndIf
			
			ShowEntity i\bg
			If KeyHit(1) Then i\txt = "" i\active = 0 change = 1
			If Main_Dedicate Then
				CameraProject(cc_cam,EntityX(i\piv,1),EntityY(i\piv,1),EntityZ(i\piv,1))
				Text ProjectedX(),ProjectedY(),EntityName(i\mesh)
			EndIf
			ShowEntity i\mesh
			key = Asc(gkey)
			If (key > 31 And key < 127) Or key >191
				change = 1
				i\txt = i\txt + gkey
			ElseIf KeyHit(14)
				change = 1
				If Len(i\txt) > 0
					i\txt = Left(i\txt,Len(i\txt)-1)
				EndIf
				i\dtimer = MilliSecs()+600
			ElseIf KeyDown(14)
				If MilliSecs() > i\dtimer
					change = 1
					If Len(i\txt) > 0
						i\txt = Left(i\txt,Len(i\txt)-1)
					EndIf
					i\dtimer = MilliSecs()+60
				EndIf
			EndIf
			If change
				FreeEntity i\mesh
				i\mesh =  Txt_Text("> "+i\txt+"_", hud_font,i\piv)
				EntityOrder i\mesh,-15
			EndIf
		Else
			i\txt = ""
			HideEntity i\bg
			HideEntity i\mesh
		EndIf
	Next
End Function

Function HUD_ClearInput()
	For i.hudinput = Each hudinput
		FreeEntity i\piv
		Delete i
	Next
End Function

Function HUD_PrintLog(txt$,r=255,g=255,b=255,time=20000,group=0)
	l.logtxt = New logtxt
	l\mesh = Txt_Text(txt$, hud_font, hud_cam_piv,0,550)
	If group=1
		PositionEntity l\mesh,-MeshWidth(l\mesh)/2,180 * main_hheight / main_hwidth,350
		l\group = 1
	Else
		EntityParent l\mesh, hud_logpiv
		PositionEntity l\mesh,0,0,0
		PlaySound hud_radio
		l\group = 0
	EndIf
	For l2.logtxt = Each logtxt
		If group = l2\group Then MoveEntity l2\mesh,0,MeshHeight(l\mesh),0
	Next
	EntityColor l\mesh,r,g,b
	EntityOrder l\mesh,-14
	l\time = time
End Function

Function HUD_WriteLog(txt$,r=255,g=255,b=255,time=20000,group=0)
	For l.logtxt = Each logtxt
		If group = l\group Then lp.logtxt = l
	Next
	l.logtxt = New logtxt
	l\mesh = Txt_Text(txt$, hud_font, hud_cam_piv)
	For l2.logtxt = Each logtxt
		If group = 1 And EntityY(l2\mesh)=EntityY(lp\mesh) Then MoveEntity l2\mesh,-MeshWidth(l\mesh)/2,0,0
	Next
	If group = 1 Then 
		PositionEntity l\mesh,EntityX(lp\mesh)+MeshWidth(lp\mesh),EntityY(lp\mesh),350
	Else
		EntityParent l\mesh, hud_logpiv
		PositionEntity l\mesh,EntityX(lp\mesh)+MeshWidth(lp\mesh),EntityY(lp\mesh),0
	EndIf
	l\group = lp\group
	EntityColor l\mesh,r,g,b
	EntityOrder l\mesh,-14
	l\time = time
End Function


Function HUD_UpdateLog()
	If hud_logshift 
		Util_Approach(hud_logpiv, -340,-350*main_ratio+100,350, .5)
		hud_logshift = 0
	Else
		Util_Approach(hud_logpiv, -340,-350*main_ratio+20,350, .5)
	EndIf
	For l.logtxt = Each logtxt
		l\time = l\time - main_mscleft
		If Main_Dedicate Then
			CameraProject(cc_cam,EntityX(l\mesh,1),EntityY(l\mesh,1),EntityZ(l\mesh,1))
			Text ProjectedX(),ProjectedY(),EntityName(l\mesh)
		EndIf
		If l\time < 400
			If l\time <= 0
				FreeEntity l\mesh
				Delete l
			Else
				EntityAlpha l\mesh,Float(l\time)/400.00
			EndIf
		EndIf
	Next
End Function

Function HUD_ClearLog()
	For l.logtxt = Each logtxt
		FreeEntity l\mesh
		Delete l
	Next
	FreeEntity hud_logpiv
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D