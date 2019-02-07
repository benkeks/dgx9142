Type hud_player
	Field s.ship
	Field name
	Field points
	Field rankm
	Field kills
	Field deaths
	Field rank
	Field star
End Type

Global hud_list,hud_lshow=0, hud_lvictories, hud_star

Function HUD_InitPlayers()
	hud_list = CreateSprite(hud_cam_piv)
	hud_star = LoadSprite(gfxd+"GUI/star.png",1+2,hud_list)
	HideEntity hud_star
	EntityBlend hud_list,2
	EntityColor hud_list,50,50,50
	ScaleSprite hud_list,300,300 * main_hheight / main_hwidth
	PositionEntity hud_list,0,0,300
	EntityOrder hud_list,-20
	hud_lshow = 0
End Function

Function HUD_AddPlayer(s.ship)
	p.hud_player	= New hud_player
	p\s				= s
	p\name			= Txt_Text(s\name, Hud_font, hud_list)
	EntityOrder p\name,-25
End Function

Function HUD_RemovePlayer(s.ship)
	For p.hud_player = Each hud_player
		If p\s = s Then
			FreeEntity p\name
			Delete p
		EndIf
	Next
End Function

Function HUD_ChangePlayerName(s.ship)
	For p.hud_player = Each hud_player
		If p\s = s
			FreeEntity p\name
			p\name			= Txt_Text(s\name, Hud_font, hud_list)
			EntityOrder p\name,-25
			p\points = 0
			p\deaths = 0
			p\kills = 0
			p\star = 0
			p\rankm = 0
		EndIf
	Next
End Function

Function HUD_ShowPlayers()
	ShowEntity hud_list
	y1 = 180 * main_hheight / main_hwidth
	y2 = 180 * main_hheight / main_hwidth
	If hud_lshow=0 Or Rand(20)=1 Then
		HUD_SortPlayers()
		If hud_lvictories Then FreeEntity hud_lvictories
		txt = Txt_Text(Int(teamid[1]\victories)+":"+Int(teamid[2]\victories), Hud_font, hud_list)
		ScaleMesh txt,4,4,4
		PositionMesh txt,-MeshWidth(txt)/2,210 * main_hheight / main_hwidth,0
		EntityOrder txt,-25
		hud_lvictories = txt
	EndIf
	For p.hud_player = Each hud_player
		If p\points Then FreeEntity p\points
		p\points = Txt_Text(p\s\points, Hud_font, p\name)
		MoveEntity p\points,80,0,0
		EntityOrder p\points,-25
		If p\deaths Then FreeEntity p\deaths
		p\deaths = Txt_Text(p\s\deaths, Hud_font, p\name)
		MoveEntity p\deaths,120,0,0
		EntityOrder p\deaths,-25
		If p\kills Then FreeEntity p\kills
		p\kills = Txt_Text(p\s\kills, Hud_font, p\name)
		MoveEntity p\kills,100,0,0
		EntityOrder p\kills,-25
		If p\rankm Then FreeEntity p\rankm
		p\rankm = Txt_Text(p\rank, Hud_font, p\name)
		MoveEntity p\rankm,-20,0,0
		EntityOrder p\rankm,-25
		
		If teamid[p\s\team]\commander = p\s Then
			If p\star = 0
				p\star = CopyEntity(hud_star,p\name)
				ShowEntity p\star
				MoveEntity p\star,-5,8,0
				ScaleSprite p\star,5,5
				EntityOrder p\star, -25
			EndIf
		ElseIf p\star <> 0
			FreeEntity p\star
			p\star = 0
		EndIf
		
		If p\s\team = 1 Then
			x = -160
			y1 = y1 - 20
			PositionEntity p\name,x,y1,0
		Else
			x = 15
			y2 = y2 - 20
			PositionEntity p\name,x,y2,0
		EndIf
		If p\s\spawntimer <= 0 Then 
			EntityColor p\name,p\s\colr,p\s\colg,p\s\colb
		Else
			EntityColor p\name,p\s\colr/2,p\s\colg/2,p\s\colb/2
		EndIf
	Next 
	hud_lshow = 1
End Function

Function HUD_SortPlayers()
	a.hud_player = First hud_player
	b.hud_player = Last hud_player
	If a.hud_player = Null  Return
	If a <> b Then Quicksort(a,b)
	For p.hud_player = Each hud_player
		i = i + 1
		p\rank = i
	Next
End Function

Function Quicksort(a.hud_player,b.hud_Player)
            s2.hud_player = A 
            s3.hud_player = A
            Repeat
                s1.hud_player=After s2
                If s1\s\points > a\s\points Or (s1\s\points = a\s\points And s1\s\kills > a\s\kills) Or (s1\s\points = a\s\points And s1\s\kills = a\s\kills And s1\s\deaths < a\s\deaths)Then
                    If s1 = b Then
                        b =Before s1
                        GoOut = 1
                    Else 
                    s2= Before s1
                    End If
                    Insert S1 Before a
                    If s3 = A Then s3=s1
                Else
                    If s1= b Then Exit
                    s2 = s1
                End If 
            Until goout
        If s3 <> a
            s1 = Before a
            If s1<> s3 Then Quicksort(s3,s1)
        End If 
        If a <> b
            s1 = After a
            If s1 <> b Then Quicksort(s1,b)
        End If 
End Function 

Function HUD_HidePlayers()
	HideEntity hud_list
	hud_lshow = 0
End Function

Function HUD_ClearPlayers()
	For p.hud_player = Each hud_player
		FreeEntity p\name
		Delete p
	Next
	hud_lvictories = 0
End Function
