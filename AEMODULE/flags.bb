Type flag
	Field name$,num
	Field team,steam
	Field takeable,beingtaken
	Field range
	Field takeper#,stakeper
	Field backup
	Field mesh,mesh2
	Field flag,progrbg,progr
	Field maps
End Type


Type spawn
	Field f.flag
	Field id
	Field Piv,maps
	Field class
	Field Typ,s.ship,flyto
	Field spawnt
	Field spawnonce
	Field range
	Field x#,y#,z#
	Field pitch#, yaw#, roll#
End Type

Global Fla_SpawnID = 0

Function Fla_ParseFlag(stream,mesh)
	f.flag = New flag
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "name",main_lang+"name"
					f\name		= paras[1]
				Case "num","id"
					f\num		= paras[1]
				Case "team"
					f\team		= paras[1]
					f\steam		= paras[1]
				Case "takeable"
					f\takeable	= paras[1]
				Case "takeper"
					f\takeper	= paras[1]
					f\stakeper	= paras[1]
				Case "range"
					f\range		= paras[1]
			End Select
		EndIf
	Until lin="}"
	
	f\mesh	= LoadSprite(gfxd+"SPRITES/point.bmp",1+2)
	f\mesh2 = Txt_Text(f\name, Hud_font, cam_cam)
	f\maps = Hud_AddMinimapObject(f\mesh,f\mesh,0,100,0,10)
	
	If f\team = 0 Or f\takeper<=0 Then
	;	HideEntity f\flag
		EntityColor f\mesh,128,128,128
		EntityColor f\mesh2,128,128,128
		Hud_MColor f\maps,128,128,128
	Else
		EntityColor f\mesh,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb
		EntityColor f\mesh2,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb
		Hud_MColor f\maps,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb
	EndIf
	ScaleSprite f\maps,2,2
	ScaleSprite f\mesh,50,50
	EntityOrder f\mesh2,-6
	
	If f\takeable = 0 Then 
		mesh3 = LoadSprite(gfxd+"GUI/untakeable.png",1+2,f\mesh2)
		EntityOrder mesh3,-7
		PositionEntity mesh3,5,-2,0
		ScaleSprite mesh3,3,3
	EndIf
	
	f\progrbg = LoadSprite(hudd$+"radprogress.png",1+2, f\mesh2)
	EntityOrder f\progrbg,-7
	PositionEntity f\progrbg,0,-5,0
	ScaleSprite f\progrbg,8.5,8.5
	EntityAlpha f\progrbg,0.8
	HideEntity f\progrbg
	f\progr = LoadSprite(hudd$+"radprogress.png",1+2, f\progrbg)
	EntityOrder f\progr,-8
	HideEntity f\progr
	
	Return f\mesh
End Function

Function Fla_FindFlagByID.flag(id)	
	For f.flag = Each flag
		If f\num = id Then Return f
	Next
End Function

Function Fla_FindFlagByPos.flag(x#,y#,z#, dist)
	r.flag = Null
	For f.flag = Each flag
		d = Util_CoordinateDistance(x,y,z, EntityX(f\mesh), EntityY(f\mesh), EntityZ(f\mesh))
		If d < dist Then 
			r.flag = f
			dist = d
		EndIf
	Next
	Return r
End Function

Function Fla_FindSpawnByID.spawn(id)
	For s.spawn = Each spawn
		If s\id = id Then Return s
	Next
End Function

Function Fla_Update()
	For f.flag = Each flag
		If f\takeable=1 And (net=0 Or net_isserver = 1)
			otakeper = f\takeper
			For sh.ship = Each ship
				If sh\team <> f\team And sh\spawntimer <=0
					If EntityDistance(sh\piv,f\mesh) < f\range Then f\takeper = f\takeper - .5*main_gspe : lastteam = sh\team
				ElseIf f\takeper < 100 And sh\spawntimer <=0
					If EntityDistance(sh\piv,f\mesh) < f\range Then f\takeper = f\takeper + .4*main_gspe : lastteam = sh\team
				EndIf
			Next
			If f\takeper < 0
				f\takeper	= -f\takeper
				If f\team = 0 
					f\beingtaken = 1
					f\team		= lastteam
					Fla_SendData(f.flag)
					;HUD_PrintLog(f\name,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb,10000,1)
					;If f\team = main_pl\team Then
					;	HUD_PrintLog(Replace(lang_flag_taken,"$",f\name),teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb)
					;Else
					;	HUD_PrintLog(Replace(lang_flag_taken_by_enemy,"$",f\name),teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb)
					;EndIf
				Else
					f\team		= 3-f\team
					Fla_SendData(f.flag)
					Event_NewEvent("flagtaken", "", f\num)
				EndIf
			ElseIf f\takeper < 90 And otakeper >= 90 And f\beingtaken = 0
				f\beingtaken = 1
				Fla_SendData(f.flag)
				HUD_PrintLog(f\name,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb,10000,1)
				;If f\team = main_pl\team Then
				;	HUD_PrintLog(Replace(lang_flag_being_taken_by_enemy,"$",f\name),255,200,0)
				;Else
				;	HUD_PrintLog(Replace(lang_flag_being_taken,"$",f\name),255,200,0)
				;EndIf
			ElseIf f\beingtaken = 1 And f\takeper > 90
				f\beingtaken = 0
				Fla_SendData(f.flag)
				HUD_PrintLog(f\name,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb,10000,1)
				;If f\team = main_pl\team Then
				;	HUD_PrintLog(Replace(lang_flag_taken,"$",f\name),teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb)
				;Else
				;	HUD_PrintLog(Replace(lang_flag_taken_by_enemy,"$",f\name),teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb)
				;EndIf
			EndIf
		EndIf
		
		If f\takeper < 90
			If f\beingtaken Then
				ShowEntity f\progrbg
				ShowEntity f\progr
				ScaleSprite f\progr,f\takeper/10,f\takeper/10
			Else
				HideEntity f\progr
				HideEntity f\progrbg
			EndIf
			If f\takeper > 10 Then EntityColor f\progr,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb
			EntityColor f\mesh,128,128,128
			EntityColor f\mesh2,128,128,128
			EntityColor hud_flags[f\num],128,128,128
			Hud_MColor f\maps,128,128,128
		Else
			HideEntity f\progr
			HideEntity f\progrbg
			EntityColor f\mesh,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb
			EntityColor f\mesh2,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb
			EntityColor hud_flags[f\num],teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb
			Hud_MColor f\maps,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb
		EndIf
		If net_update = 1 And Rand(4)=2 Then Fla_SendData(f.flag,1)
		
		If f\takeper > 90 Then teamid[f\team]\flagcount = teamid[f\team]\flagcount + 1
		
		PositionEntity f\mesh2,EntityX(f\mesh),EntityY(f\mesh),EntityZ(f\mesh),1
		
		;PointEntity f\mesh2,cam_cam
		;RotateEntity f\mesh2,EntityPitch(f\mesh2),EntityYaw(f\mesh2),EntityRoll(cam_cam)
		fa# = EntityDistance(f\mesh2,cam_cam)
		If fa# <> 0.00 Then PositionEntity f\mesh2,EntityX(f\mesh2)*350/fa,EntityY(f\mesh2)*350/fa,EntityZ(f\mesh2)*350/fa
		;MoveEntity f\mesh2,
	Next
	
	If net = 0 Or net_isserver = 1
		For s.spawn = Each spawn
			If s\typ = 2
				If s\f\team <> 0
					If s\s = Null
						Fla_SendData(s\f)
						c.class = Race_GetClassByID(s\class,s\f\team,2)
						s\s = Object.ship(Shi_CreateShip(0,0,0,s\class,c\name,s\f\team,2,2))
						s\s\team	 = s\f\team
						s\s\selclass = s\class
						s\s\selspawn = s\id
						s\s\spawntimer = s\spawnt
						If s\flyto
							ki.kiplayer = KI_GetKI(s\s)
							ki\globaction = 2
							f.flag = Fla_FindFlagByID(s\flyto)
							ki\target = CreatePivot(f\mesh)
						EndIf
						Shi_SendSpawnData(s\s)
						;Race_Equip(Handle(s\s),s\s\team,s\s\selclass)
					ElseIf s\flyto
						f.flag = Fla_FindFlagByID(s\flyto)
						If EntityDistance(f\mesh,s\s\piv) < f\range Then
							wa = FX_CreateWarp(s\s,1)
							c.class = Race_GetShipsClass(s\s\shc\classid,s\s\team,s\s\typ)
							teamid[3-f\team]\tickets = teamid[3-f\team]\tickets - c\tickets
							Shi_DeleteShip(Handle(s\s))
							s\s = Null
						EndIf
					EndIf
				EndIf 
			EndIf
		Next
	EndIf
End Function

Function Fla_Reset()
	For f.flag = Each flag
		f\team		= f\steam
		f\takeper	= f\stakeper
		f\beingtaken= 0
	Next
End Function

Function Fla_Clear()
	For f.flag = Each flag
		FreeEntity f\mesh
		Delete f
	Next
	
	Delete Each spawn
	
	Fla_SpawnID = 0
End Function

Function Fla_ParseSpawn(stream)
	s.spawn = New spawn
	s\typ	= 1
	s\spawnt= 5000
	
	Fla_SpawnID = Fla_SpawnID + 1
	s\id	= Fla_SpawnID
	
	s\pitch = -9999
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "flag"
					s\f		= Fla_FindFlagByID(paras[1])
				Case "class"
					s\class	= paras[1]
				Case "type","typ"
					s\typ	= paras[1]
				Case "range"
					s\range	= paras[1]
				Case "x"
					s\x		= paras[1]
				Case "y"
					s\y		= paras[1]
				Case "z"
					s\z		= paras[1]
				Case "pitch"
					s\pitch	= paras[1]
				Case "yaw"
					s\yaw	= paras[1]
				Case "roll"
					s\roll	= paras[1]
				Case "spawntime"
					s\spawnt= paras[1]
				Case "spawnonce"
					s\spawnonce = paras[1]
				Case "flyto"
					s\flyto	= paras[1]
			End Select
		EndIf
	Until lin="}"
	
	If s\typ = 2 And s\class = 1 And s\f\backup = 0 Then
		mesh3 = LoadSprite(gfxd+"GUI/backup.png",1+2,s\f\mesh2)
		EntityOrder mesh3,-7
		PositionEntity mesh3,10,-2,0
		ScaleSprite mesh3,3,3
		EntityAlpha mesh3,0.3
		s\f\backup = mesh3
	EndIf
	
	s\piv = CreatePivot(s\f\mesh)
	PositionEntity s\piv,s\x,s\y,s\z
	s\maps = Hud_AddMinimapObject(s\piv,hud_spawn,hud_spawnpiv,50)
End Function

Function Fla_SendData(f.flag,a=0)
	If hud_mode = 1 And hud_showcselect = 1 Then HUD_SetSpawns(main_pl\selclass,main_pl\team)
	If net_isserver
		AddUDPByte(C_FlagData)
		AddUDPByte(f\num)
		AddUDPByte(f\team+a*10)
		AddUDPByte(f\takeper)
	EndIf
End Function

Function Fla_GetData()
	num = ReadUDPByte()
	For f.flag = Each flag 
		If f\num = num Then
			f\team			= ReadUDPByte()
			t = 0
			If f\team >= 10 Then t = 1 : f\team = f\team - 10
			f\takeper		= ReadUDPByte()
			If net_ready=1 And f\team <> 0 And t=0 Then HUD_PrintLog(f\name,teamid[f\team]\colr,teamid[f\team]\colg,teamid[f\team]\colb,10000,1)
		EndIf
	Next
	If hud_mode = 1 Then
		If main_pl <> Null And hud_showcselect = 1 Then HUD_SetSpawns(main_pl\selclass,main_pl\team)
	EndIf
End Function