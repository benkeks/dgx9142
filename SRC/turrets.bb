Type turret
	Field s.ship, id
	Field weapsig, group
	Field w.weapon
	Field base1, base2
	Field gun
	Field target.ship
	Field cacheddist#
End Type


Function Tur_Create.turret(s.ship, id, w.weapon)
	t.turret= New turret
	t\s		= s
	t\id	= id
	t\w		= w
	
	t\base1	= CopyEntity(t\w\base1, s\mesh)
	t\base2	= CopyEntity(t\w\base2, t\base1)
	t\gun	= CopyEntity(t\w\gun, t\base2)
	
	If main_texdetail=2 Then
		EntityFX t\base1,1 : EntityTexture t\base1,shi_specular2,0,2 : EntityTexture t\base1,shi_specular,0,3
		EntityFX t\base2,1 : EntityTexture t\base2,shi_specular2,0,2 : EntityTexture t\base2,shi_specular,0,3
		EntityFX t\gun,1 : EntityTexture t\gun,shi_specular2,0,2 : EntityTexture t\gun,shi_specular,0,3
	EndIf
	
	t\weapsig = s\shc\weapsig
	t\group	=  weapsigi(t\weapsig,id,1)
	
	PositionEntity t\base1,weapsigf(t\weapsig,id,0),weapsigf(t\weapsig,id,1),weapsigf(t\weapsig,id,2)
	TurnEntity t\base1, weapsigf(t\weapsig,id,3),weapsigf(t\weapsig,id,4),0
	TurnEntity t\gun, w\defaultpitch,0,0
End Function

Function Tur_ApplyTurrets(s.ship)
	id		= s\shc\weapsig
	
	For i = 1 To 10
		twg = weapsigi(id,i,1) 
		If weapsigi(id,i,0)=3 ; geschütz?
			w.weapon = weaponid[s\weapgroup[twg]]
			If w\base1 <> 0 Then Tur_Create(s, i, w)
		EndIf
	Next
End Function

Function Tur_Free(t.turret)
	FreeEntity t\base1
	Delete t
End Function

Function Tur_Remove(s.Ship)
	For t.turret = Each turret
		If t\s = s Then Tur_Free(t)
	Next
End Function

Function Tur_Update()
	For t.turret = Each turret
		If t\s\spawntimer > 0 Then
			HideEntity t\base1
		Else
			TFormPoint 0,0,0,t\base1, cc_cam
			If TFormedZ()>-20 And TFormedZ()<1500 / Main_CriticalFPS Then
				ShowEntity t\base1
			Else
				HideEntity t\base1
			EndIf
			If t\target = Null Then
				If Rand(10)=4 And (net_isserver=1 Or net=0)
					dist# = 1001
					For s2.ship = Each ship
						If s2\team <> t\s\team And s2\spawntimer <= 0
							edist# = EntityDistanceB(s2\mesh,t\base1,dist+500)-200*(s2\shc\typ=4)-300*(t\s\target=s2)
							If edist < dist
								distt#	= edist# / t\w\speed#
								TranslateEntity s2\piv,s2\dx*distt#,s2\dy*distt#,s2\dz*distt#
								TFormPoint 0,0,0, s2\piv, t\base1
								If TFormedY()>0 Then
									dist	= distt
									t\target= s2
									t\cacheddist = dist
								EndIf
								TranslateEntity s2\piv,-s2\dx*distt#,-s2\dy*distt#,-s2\dz*distt#
							EndIf
						EndIf
					Next
					If net_isserver Then Tur_SendAimAt(t)
				EndIf
			Else
				If Rand(10)=1 Then
					t\cacheddist =  EntityDistance(t\target\mesh,t\base1)-300*(t\s\target=s2)
				EndIf
				edist# = t\cacheddist
				
				If edist < 1000 And t\target\spawntimer <= 0
					distt#	= edist# / t\w\speed#
					TranslateEntity t\target\piv,t\target\dx*distt#,t\target\dy*distt#,t\target\dz*distt#
					TFormPoint 0,0,0, t\target\piv, t\base1
					If TFormedY()<0 Then
						TranslateEntity t\target\piv,-t\target\dx*distt#,-t\target\dy*distt#,-t\target\dz*distt#
						t\target = Null
					Else
						dy# = ATan2(-TFormedX(),TFormedZ()) - EntityYaw(t\base2)
						If dy <= -180 Then dy = dy + 360
						If dy >  +180 Then dy = dy - 360
						TFormPoint 0,0,0, t\target\piv, t\gun
						dp# = ATan2(-TFormedY()+t\w\gunoffsetz,TFormedZ())
						If dp <= -180 Then dp = dp + 360
						If dp >  +180 Then dp = dp - 360
						If Abs(dy) < t\w\targetspeed * main_gspe Then
							RotateEntity t\base2, EntityPitch(t\base2), EntityYaw(t\base2)+dy, EntityRoll(t\base2)
						Else
							RotateEntity t\base2, EntityPitch(t\base2), EntityYaw(t\base2)+Sgn(dy) * t\w\targetspeed * main_gspe, EntityRoll(t\base2)
						EndIf
						If Abs(dp) < t\w\targetspeed * main_gspe Then
							RotateEntity t\gun, EntityPitch(t\gun)+dp, EntityYaw(t\gun), EntityRoll(t\gun)
						ElseIf Abs(dp) < 90
							RotateEntity t\gun, EntityPitch(t\gun)+Sgn(dp) * t\w\targetspeed * main_gspe, EntityYaw(t\gun), EntityRoll(t\gun)
						EndIf
						TranslateEntity t\target\piv,-t\target\dx*distt#,-t\target\dy*distt#,-t\target\dz*distt#
						
						If Abs(dy)+Abs(dp) < 10 Then
							If t\s\weapreload[t\id]<=.1 And t\s\weapammo[t\group] >= t\w\neAmmo And t\s\power > t\w\NePower And (net=0 Or net_isserver=1) Then
								TFormPoint 0,t\w\gunoffsety,t\w\gunoffsetz, t\gun, 0
								c = wea_count
								Wea_CreateShoot(  TFormedX(), TFormedY(), TFormedZ(), EntityPitch(t\gun,1), EntityYaw(t\gun,1), 0, t\s\weapgroup[ t\group ],Handle(t\target),t\s,dist2, -1,1  )
								t\s\weapreload[t\id] = t\w\reload
								t\s\weapammo[t\group] = t\s\weapammo[t\group] - t\w\neAmmo
								t\s\power = t\s\power - t\w\NePower
								If net_isserver Then Tur_SendFire(t,c)
							EndIf
						EndIf
					EndIf
					If EntityPitch(t\gun) > -t\w\minpitch
						RotateEntity t\gun, -t\w\minpitch, 0,0
					EndIf
				ElseIf net=0 Or net_isserver=1
					t\target = Null
					If net_isserver Then Tur_SendAimAt(t)
				EndIf
			EndIf
		EndIf
	Next
End Function

Function Tur_SendAimAt(t.turret)
	If net_isserver=1
		AddUDPByte( C_AimAt )
		AddUDPByte( t\s\id )
		AddUDPByte( t\id )
		If t\target=Null Then
			AddUDPByte( 255 )
		Else
			AddUDPByte( t\target\id )
		EndIf
	EndIf
End Function

Function Tur_GetAimAt()
	s.ship	= ships(ReadUDPByte())
	turret	= ReadUDPByte()
	target	= ReadUDPByte()
	If s <> Null
		For t.turret = Each turret
			If t\id = turret And t\s = s Then
				If target=255 Then
					t\target=Null
				Else
					t\target = ships(target)
				EndIf
				Exit
			EndIf
		Next
	EndIf
End Function

Function Tur_SendFire(t.turret,c)
	If net_isserver = 1
		AddUDPByte( C_TurFire )
		AddUDPByte( t\s\id )
		AddUDPByte( t\id )
		AddUDPShort(c)
	EndIf
End Function

Function Tur_GetFire()
	s.ship	= ships(ReadUDPByte())
	turret	= ReadUDPByte()
	wid		= ReadUDPShort()
	If s <> Null
		For t.turret = Each turret
			If t\id = turret And t\s = s Then
				TFormPoint 0,t\w\gunoffsety,t\w\gunoffsetz, t\gun, 0
				Wea_CreateShoot(  TFormedX(), TFormedY(), TFormedZ(), EntityPitch(t\gun,1), EntityYaw(t\gun,1), 0, t\s\weapgroup[ t\group ],Handle(t\target),t\s,dist2, wid,1  )
				Exit
			EndIf
		Next
	EndIf
End Function