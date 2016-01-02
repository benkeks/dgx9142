Type kiplayer
	Field occupation ; Basis verteidigen, Angreifen und Ähnliches
	Field globaction ; 1 = attack, 2 = flyto, 3 = abdrehn, 4 = flucht
	Field action,dodge, dodgetime ; 1 = move, 2 = attack
	Field target ; Zielentity
	Field ttarget
	Field tars.ship ; Zieltype, falls auf Schiff gerichtet
	Field sh.ship ; schiff, zu dem die KI gehört :)
	Field attacktime
	Field turnp#, turny#
End Type

Global ki_tpiv

Function KI_AddKIPlayer.kiplayer(s.ship)
	ki.kiplayer = New kiplayer
	ki\sh.ship	= s
	Return ki
End Function

Function KI_TurnShip(ki.kiplayer, dp#, dy#)
	Local alignSpe# = .7^main_gspe
	
	If Abs(dp) < 1 Then dp = 0
	If Abs(dy) < 1 Then dy = 0
	
	ki\turnp	= Util_MinMax( ki\turnp*alignSpe + Sgn(dp) * .2 * (alignSpe-1)/.3, -1,1 ) 
	ki\turny	= Util_MinMax( ki\turny*alignSpe + Sgn(dy) * .2 * (alignSpe-1)/.3, -1,1 ) 
	
	If Abs(ki\turnp) + Abs(dp)/10.0 < 0.2 Then ki\turnp = ki\turnp * .5^main_gspe
	If Abs(ki\turny) + Abs(dy)/10.0 < 0.2 Then ki\turny = ki\turny * .5^main_gspe
	
	ki\sh\tspitch	= Util_MinMax( ki\sh\tspitch*alignSpe#		+ (1.0-alignSpe#) * ki\turnp*ki\sh\shc\turnspeed,	-ki\sh\shc\turnspeed,ki\sh\shc\turnspeed)
	ki\sh\tsyaw	= Util_MinMax( ki\sh\tsyaw*alignSpe#		+ (1.0-alignSpe#) * ki\turny*ki\sh\shc\turnspeed,	-ki\sh\shc\turnspeed,ki\sh\shc\turnspeed)
End Function

Function KI_Update()
	For ki.kiplayer = Each kiplayer
		If ki\sh\spawntimer <= 0
			If ki\dodge
				PositionEntity ki\dodge, ki\sh\x,ki\sh\y,ki\sh\z
				MoveEntity ki\dodge,0,0,5
				
				dy# = DeltaYaw(ki\sh\piv,ki\dodge)
				dp# = DeltaPitch(ki\sh\piv,ki\dodge)
				
				If Abs(dy)+Abs(dp)<90
					If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
					
					If Abs(dy)+Abs(dp)<20.0
						If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
							ki\sh\burnafter = 1
						EndIf
					Else
						ki\sh\burnafter = 0
					EndIf
				EndIf
				
				KI_TurnShip(ki, dp, dy)
				
				ki\dodgetime = ki\dodgetime + main_mscleft	
				
				If Rand(10)=1 Then
					HideEntity ki\sh\piv
					If LinePick(ki\sh\x,ki\sh\y,ki\sh\z, ki\sh\dx*350,ki\sh\dy*350,ki\sh\dz*350, ki\sh\shc\size*2+2) = 0 Then
						FreeEntity ki\dodge
						ki\dodge = 0
					EndIf
					ShowEntity ki\sh\piv
				ElseIf ki\dodgetime > 1500
					FreeEntity ki\dodge
					ki\dodge = 0
				EndIf
			Else
				Select ki\sh\shc\typ
				Case 1,6 ; Jäger
					KI_Ship(ki)
				Case 2 ; Bomber
					KI_Bomber(ki)
				Case 3 ; kreuzer
					KI_BigShip(ki)
				Case 4 ; kanone
					KI_Cannon(ki)
				Case 5 ; scout
					KI_Scout(ki)
				Case 7 ; cargo
					KI_Cargo(ki)
				End Select
				
				Shi_SetTarget(ki\sh, ki\tars)
				
				;ausweichen?
				ki\dodgetime = 0
				If ki\sh\shc\size < 20 And Rand(20)=1 Then
					HideEntity ki\sh\piv
					If LinePick(ki\sh\x,ki\sh\y,ki\sh\z, ki\sh\dx*250,ki\sh\dy*350,ki\sh\dz*250, ki\sh\shc\size*2+2) <> 0
						pitch# = EntityPitch(ki\sh\piv)
						yaw# = EntityYaw(ki\sh\piv)
						roll# = EntityRoll(ki\sh\piv)
						For i1 = 0 To 3
							For i2 = 0 To 3
								RotateEntity ki\sh\piv,pitch,yaw,roll
								TurnEntity ki\sh\piv,i1 * 15 * (Rand(0,1)*2-1), i2 * 15 * (Rand(0,1)*2-1),0
								TFormVector 0,0,ki\sh\realspeed, ki\sh\piv, 0
								If LinePick(ki\sh\x,ki\sh\y,ki\sh\z, TFormedX()*350,TFormedY()*350,TFormedZ()*350, ki\sh\shc\size*2+2) = 0
									;RotateEntity ki\sh\piv,pitch,yaw,roll
									;TurnEntity ki\sh\piv,i1 * 45 * (Rand(0,1)*2-1), i2 * 45 * (Rand(0,1)*2-1),0; what was the point of this new rotation?!
									ki\dodge = CreatePivot(ki\sh\piv)
									EntityParent ki\dodge,0
									i1 = 4
									i2 = 4
								EndIf
							Next
						Next
						RotateEntity ki\sh\piv,pitch,yaw,roll
					EndIf
					ShowEntity ki\sh\piv
				EndIf
				
				; spezialwaffen
				If Rand(40)=1
					For i = 0 To 4
						weap = ki\sh\weapgroup[i]
						If weaponid[weap] <> Null Then 
							If weaponid[weap]\SpawnShipClass <> 0 Or (weaponid[weap]\actrange > 20 And weaponid[weap]\sspeed < 0.1) ; mine / geschütz setzen?
								For i2 = 1 To 10
									If weapsigi(ki\sh\shc\weapsig,i2,1) = i Then Exit
								Next
								If i2 < 11 Then
									If ki\sh\weapreload[i2] <= 0 Then
										For f.flag = Each flag
											dist# = EntityDistanceB(f\mesh, ki\sh\piv,1001)
											If dist < 1000 Then
												If Rand(0,dist/20)=0 Then Shi_Fire(ki\sh,weapsigi(ki\sh\shc\weapsig,i2,0),Null)
											EndIf
										Next
										If ki\sh\weapreload[i2] <= 0
											t# = 0
											For s2.ship = Each ship
												If s2\team <> ki\sh\team
													dist# = EntityDistanceB(s2\piv, ki\sh\piv,1501)
													If dist < 1500 Then t = t + (1500-dist)
												EndIf
											Next
											If t > 5000 - (weaponid[weap]\actrange > 20 And weaponid[weap]\sspeed < 0.1)*4000 Then Shi_Fire(ki\sh,weapsigi(ki\sh\shc\weapsig,i2,0),Null)
										EndIf
									EndIf
								EndIf
							ElseIf (weaponid[weap]\emp > 20 And weaponid[weap]\range < 70 ) ; emp gegen raketen etc auslösen
								For i2 = 1 To 10
									If weapsigi(ki\sh\shc\weapsig,i2,1) = i Then Exit
								Next
								If i2 < 11 Then
									If ki\sh\weapreload[i2] <= 0 Then
										t = ki\sh\indanger
										For sho.shoot = Each shoot
											If sho\par <> Null
												If sho\par\team <> ki\sh\team And sho\swc\hdamage+sho\swc\sdamage>700 Then
													dist = EntityDistanceB(sho\mesh,ki\sh\piv,weaponid[weap]\emp-19)
													If dist < weaponid[weap]\emp-20 Then t = t - (weaponid[weap]\emp - dist) * (sho\swc\hdamage+sho\swc\sdamage) / 200
												EndIf
											EndIf
										Next
										If t < weaponid[weap]\emp Then Shi_Fire(ki\sh,weapsigi(ki\sh\shc\weapsig,i2,0),Null)
									EndIf
								EndIf
							EndIf
						EndIf
					Next
				EndIf
			EndIf
		ElseIf (ki\sh\selspawn = 0 Or ki\sh\selclass = 0)
			If ki\sh\team = 1 Then fact# = 1.0 / Game_TeamBalance Else fact# = Game_TeamBalance
			If Team_CountShips(ki\sh\team,ki\sh) > Float(Team_CountShips(3-ki\sh\team,Null)) * fact#
				Team_JoinTeam(ki\sh,3-ki\sh\team)
				ki\sh\selspawn = 0
				ki\sh\selclass = 0
				If net_isserver Then Shi_SendSpawnData(ki\sh)
			Else
				If ki\sh\selclass = 0 Then
					Local tempclass[5], newclass = 0
					tempclass[0] = 100
					For sh.ship = Each ship
						If sh\selclass <> 0 And sh\typ = ki\sh\typ And sh\team = ki\sh\team
							tempclass[sh\selclass] = tempclass[sh\selclass] + 1
						EndIf
					Next
					For i = 1 To 4
						If tempclass[i] < tempclass[newclass] Then newclass = i
					Next
					If newclass <> 0 Then 
						ki\sh\selclass = newclass
					Else
						ki\sh\selclass = Rand(1,4)
					EndIf
					Shi_SelectClass(ki\sh,ki\sh\selclass)
				EndIf
				ospawn = ki\sh\selspawn
				tspawn = ospawn
				trating = 0
				For s.spawn = Each spawn
					If s\class = ki\sh\selclass And s\typ = 1 And s\f\team = ki\sh\team And s\f\takeper>90 Then
						tf# = 0
						For sh.ship = Each ship
							If sh\spawntimer <= 0 Then
								dist# = EntityDistanceB(sh\piv, s\piv, 2001)
								If dist < 2000
									fact# = 0.1
									Select ki\sh\shc\typ
									Case 1,5 ; jäger oder scout mag alle feindschiffe
										If ki\sh\team <> sh\team Then fact = 2
									Case 2 ; bomber mag große schiffe
										If ki\sh\team <> sh\team Then fact = sh\shc\size
									Case 6 ; support mag alle freundschiffe
										If ki\sh\team = ki\sh\team Then fact = 2
									End Select
									tf = tf + (2000-dist) * fact
								EndIf
							EndIf
						Next
						For f.flag = Each flag
							If f\team <> ki\sh\team Or f\takeper <= 90 Then
								dist# = EntityDistanceB(s\piv, f\mesh, 8001)
								If dist < 8000
									tf = tf + (8000-dist)
								EndIf
							EndIf
						Next
						tf = tf * Rnd(0.5,1.3)
						If tf >= trating Then tspawn = s\id : trating = tf
					EndIf
				Next
				ki\sh\selspawn = tspawn
				If ospawn <> ki\sh\selspawn And net_isserver Then Shi_SendSpawnData(ki\sh)
			EndIf
		ElseIf Rand(100)=1
			s.spawn = Fla_FindSpawnByID(ki\sh\selspawn)
			If s <> Null
				If s\s <> ki\sh Then
					ki\sh\selspawn = 0
					If Rand(5) = 1 Then ki\sh\selclass = 0
				EndIf
			EndIf
		EndIf
		
		If ki\sh\spawntimer > 0
			If ki\dodge Then FreeEntity ki\dodge : ki\dodge = 0
		EndIf
		;CameraProject(cc_cam,EntityX(ki\sh\piv),EntityY(ki\sh\piv),EntityZ(ki\sh\piv))
		;If ProjectedZ()>0
		;	Text ProjectedX(),ProjectedY(),EntityDistance(ki\sh\piv,main_pl\piv)+" "+ki\sh\name$+" "+ki\globaction
		;EndIf
	Next
End Function

Function KI_Ship(ki.kiplayer)
	Select ki\sh\order
	Case ORDER_ATTACK
		ki\tars = ki\sh\oship
		ki\target = ki\tars\piv
		ki\globaction = 1
	Case ORDER_MOVETO
		ki\target = ki\sh\opiv
		ki\globaction = 2
	End Select
	
	Select ki\globaction
	Case 0 ; wait / find
		If Rand(0,10) = 5
			tdist# = 1201
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0
					dist# = EntityDistanceB(ki\sh\piv,zs\piv, 1201)*((zs\stealthed>0)*2+1)
					If dist <= 1200 And dist < tdist
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=1200 And thandle<>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
			Else
				tdist# = 30001
				thandleM = -1
				For f.flag = Each flag
					dist = EntityDistanceB(ki\sh\piv,f\mesh, tdist+1)
					If (f\team <> ki\sh\team Or f\takeper<90) And tdist > dist And Rand(0,1)
						tdist = dist
						thandleM = f\mesh
						For c.conquest = Each conquest
							
						Next
					EndIf
				Next
				If tdist <= 30000 And thandleM <> -1
					ki\target		= CreatePivot(thandleM)
					ki\globaction	= 2 ; flyto
				EndIf
			EndIf
			If ki\sh\shc\typ = 3
				Shi_Fire(ki\sh,3, Null)
			EndIf
		EndIf
	Case 1 ; attack
		dist# = EntityDistance(ki\sh\piv,ki\target)
		distt#	= dist# / (weaponid[ki\sh\weapgroup[1]]\speed#+ki\sh\frontspeed*1.1)
		PositionEntity ki_tpiv,ki\tars\x,ki\tars\y,ki\tars\z
		TranslateEntity ki_tpiv,ki\tars\dx*distt#,ki\tars\dy*distt#,ki\tars\dz*distt#,1
		dy# = DeltaYaw(ki\sh\piv,ki_tpiv)
		dp# = DeltaPitch(ki\sh\piv,ki_tpiv)
		
		If dist>1240 And ki\sh\order <> ORDER_ATTACK
			ki\tars.ship	= Null
			ki\target		= 0
			ki\globaction	= 0
		ElseIf ki\tars\spawntimer>0
			ki\globaction = 0
		ElseIf dist/ki\sh\zzs < 1 And ki\sh\zzs > ki\tars\zzs
			ki\sh\zzs = ki\sh\zzs -ki\sh\shc\speeddown*main_gspe
		ElseIf dist>50 And Abs(dy)+Abs(dp)<40.0/(Abs(Float(ki\sh\zzs))+1)
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			If dist > 80 And Abs(dy)+Abs(dp)<10.0
				If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
					ki\sh\burnafter = 1
				EndIf
			Else
				ki\sh\burnafter = 0
			EndIf
		ElseIf dist/ki\tars\shc\size<20
			ki\globaction = 3 ; abdrehn 
		ElseIf Abs(dy)+Abs(dp)>120/(Abs(Float(ki\sh\zzs))+1) And dist < 200
			If ki\sh\zzs>ki\sh\shc\lowspeed And map_atmo = 0 Then ki\sh\zzs = ki\sh\zzs-ki\sh\shc\speeddown*main_gspe
		EndIf
		
; 		spe# = main_gspe
; 		If spe>3/ki\sh\shc\turnspeed Then spe = 3
; 		If Abs(dy) > ki\sh\shc\turnspeed*spe*.3
; 			ki\sh\tsyaw = ki\sh\tsyaw-Sgn(ki\sh\tsyaw-dy*.1)*.1*spe*ki\sh\shc\turnspeed
; 		Else
; 			ki\sh\tsyaw = dy*.1
; 		EndIf
; 		If Abs(dp) > ki\sh\shc\turnspeed*spe*.3
; 			ki\sh\tspitch = ki\sh\tspitch-Sgn(ki\sh\tspitch-dp*.1)*.1*spe*ki\sh\shc\turnspeed
; 		Else
; 			ki\sh\tspitch = dp*.1
; 		EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		If Abs(dy)+Abs(dp)<10 And Rand(7)=4
			Shi_Fire(ki\sh,1,ki\tars)
			Shi_Fire(ki\sh,2,ki\tars)
			If ki\sh\shc\typ = 3
				Shi_Fire(ki\sh,3,ki\tars)
			EndIf
		EndIf
		If ki\sh\shc\typ = 3
			Shi_Fire(ki\sh,3,ki\tars)
		EndIf
	Case 2 ; fly to
		ki\ttarget = ki\target
		
		dist# = EntityDistance(ki\sh\piv,ki\ttarget)
		
		For g.gate = Each gate
			If EntityDistanceB(g\mesh,ki\sh\piv,dist)+EntityDistanceB(g\g\mesh,ki\target,dist) < dist
				ki\ttarget = g\mesh
				dist# = EntityDistance(ki\sh\piv,ki\ttarget)
			EndIf 
		Next
		
		dist# = EntityDistance(ki\sh\piv,ki\ttarget)
		dy# = DeltaYaw(ki\sh\piv,ki\ttarget)
		dp# = DeltaPitch(ki\sh\piv,ki\ttarget)
		
		If dist>100 And Abs(dy)+Abs(dp)<60
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			
			If dist > 120 And Abs(dy)+Abs(dp)<10.0
				If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
					ki\sh\burnafter = 1
				EndIf
			Else
				ki\sh\burnafter = 0
			EndIf
			
		ElseIf dist<80 And ki\target = ki\ttarget
			If map_atmo = 0 
				ki\sh\zzs = ki\sh\zzs-ki\sh\shc\speeddown*main_gspe
			EndIf
			If ki\sh\order = ORDER_MOVETO 
				ki\sh\order = ORDER_STOP
				ki\globaction = 0
				ki\target = 0
				FreeEntity ki\sh\opiv
				ki\sh\opiv = 0
			EndIf
		EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		If Rand(0,10) = 5 And ki\sh\order <> ORDER_MOVETO 
			tdist# = 901
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0
					dist# = EntityDistanceB(ki\sh\piv,zs\piv,tdist) * ((zs\stealthed>0)*2+1)
					If dist <= 900 And dist < tdist
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=900 And thandle>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
			EndIf
		ElseIf Rand(0,10) = 1 And ki\globaction = 2
			For f.flag = Each flag
				If f\mesh = GetParent(ki\target) Then
					If f\team = ki\sh\team And f\takeper > 90 Then
						ki\globaction = 0
					EndIf
				EndIf
			Next
		EndIf
	Case 3 ; abdrehn
		dist# = EntityDistance(ki\sh\piv,ki\target)
		TurnEntity ki\sh\piv,0,180,0
		dy# = DeltaYaw(ki\sh\piv,ki\target)
		dp# = DeltaPitch(ki\sh\piv,ki\target)
		TurnEntity ki\sh\piv,0,-180,0
		
		If dist*ki\tars\shc\size>100
			ki\globaction = 1
		Else
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			If dist < 200 And Abs(dy)+Abs(dp)<10.0
				If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
					ki\sh\burnafter = 1
				EndIf
			Else
				ki\sh\burnafter = 0
			EndIf
		EndIf
		
		KI_TurnShip(ki, dp, dy)
	End Select
End Function


Function KI_Bomber(ki.kiplayer)
	Select ki\sh\order
	Case ORDER_ATTACK
		ki\tars = ki\sh\oship
		ki\target = ki\tars\piv
		ki\globaction = 1
	Case ORDER_MOVETO
		ki\target = ki\sh\opiv
		ki\globaction = 2
	End Select

	
	ki\attacktime = ki\attacktime + main_mscleft
	Select ki\globaction
	Case 0 ; wait / find
		If Rand(0,10) = 5
			tdist# = 301
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0
					dist# = EntityDistance(ki\sh\piv,zs\piv)/(zs\shc\size+(zs\shc\typ=4)*10) * ((zs\stealthed>0)*2+1)
					If dist <= 300 And dist < tdist
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=300 And thandle>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
				ki\attacktime	= 0
			Else
				tdist# = 30001
				thandle = -1
				For f.flag = Each flag
					dist = EntityDistance(ki\sh\piv,f\mesh)
					If (f\team <> ki\sh\team Or f\takeper<90) And tdist > dist And Rand(0,1)
						tdist = EntityDistance(ki\sh\piv,f\mesh)
						thandle = f\mesh
					EndIf
				Next
				If tdist <= 30000 And thandle <> -1
					ki\target		= CreatePivot(thandle)
					ki\globaction	= 2 ; flyto
				EndIf
			EndIf
			If ki\sh\shc\typ = 3
				Shi_Fire(ki\sh,3,Null)
			EndIf
		EndIf
	Case 1 ; attack
		ki\ttarget = ki\target
		
		dist# = EntityDistance(ki\sh\piv,ki\target)
		
		For g.gate = Each gate
			If EntityDistance(g\mesh,ki\sh\piv)+EntityDistance(g\g\mesh,ki\target) < dist
				ki\ttarget = g\mesh
				dist# = EntityDistance(ki\sh\piv,ki\ttarget)
			EndIf 
		Next
		
		If ki\target = ki\ttarget Then
			distt#	= dist# / (weaponid[ki\sh\weapgroup[1]]\speed#+ki\sh\frontspeed*1.1)
			PositionEntity ki_tpiv,ki\tars\x,ki\tars\y,ki\tars\z
			TranslateEntity ki_tpiv,ki\tars\dx*distt#,ki\tars\dy*distt#,ki\tars\dz*distt#,1
			dy# = DeltaYaw(ki\sh\piv,ki_tpiv)
			dp# = DeltaPitch(ki\sh\piv,ki_tpiv)
			
			If (dist>420*(ki\tars\shc\size+(ki\tars\shc\typ=4)*30) Or ki\attacktime > 4000*(ki\tars\shc\size+(ki\tars\shc\typ=4)*12)) And ki\sh\order <> ORDER_ATTACK
				ki\tars.ship	= Null
				ki\target		= 0
				ki\globaction	= 0
			ElseIf ki\tars\spawntimer>0
				ki\globaction = 0
			ElseIf dist<50+20*ki\tars\shc\size And ki\sh\weapreload[ki\sh\shc\mainweap]>500
				TurnEntity ki\sh\piv,0,180,0
				dy# = DeltaYaw(ki\sh\piv,ki\target)
				dp# = DeltaPitch(ki\sh\piv,ki\target)
				TurnEntity ki\sh\piv,0,-180,0
				If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			ElseIf dist>50+10*ki\tars\shc\size And Abs(dy)+Abs(dp)<30
				If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
				If dist > 90 And Abs(dy)+Abs(dp)<10.0
					If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
						ki\sh\burnafter = 1
					EndIf
				Else
					ki\sh\burnafter = 0
				EndIf
			ElseIf dist<40+7*ki\tars\shc\size Or Abs(dy)+Abs(dp)>160
				If ki\sh\zzs>ki\sh\shc\lowspeed And map_atmo = 0 Then ki\sh\zzs = ki\sh\zzs-ki\sh\shc\speeddown*main_gspe
			EndIf
			
			KI_TurnShip(ki, dp, dy)
			
			If Abs(dy)+Abs(dp)<10 And dist < ki\sh\shc\attackrange*2 And Rand(7)=4
				Shi_Fire(ki\sh,1,ki\tars)
				If dist < ki\sh\shc\attackrange * ki\sh\shc\hitpoints / ki\sh\hitpoints
					Shi_Fire(ki\sh,2,ki\tars)
				EndIf
				If ki\sh\shc\typ = 3
					Shi_Fire(ki\sh,3,ki\tars)
				EndIf
			EndIf
		Else
			dy# = DeltaYaw(ki\sh\piv,ki\ttarget)
			dp# = DeltaPitch(ki\sh\piv,ki\ttarget)
			
			If ki\tars\spawntimer>0
				ki\globaction = 0
			ElseIf  Abs(dy)+Abs(dp)<30
				If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
				If dist > 90 And Abs(dy)+Abs(dp)<10.0
					If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
						ki\sh\burnafter = 1
					EndIf
				Else
					ki\sh\burnafter = 0
				EndIf
			EndIf
			KI_TurnShip(ki, dp, dy)
		EndIf
		If ki\sh\shc\typ = 3
			Shi_Fire(ki\sh,3,ki\tars)
		EndIf
	Case 2 ; fly to
		ki\ttarget = ki\target
		
		dist# = EntityDistance(ki\sh\piv,ki\ttarget)
		
		For g.gate = Each gate
			If EntityDistance(g\mesh,ki\sh\piv)+EntityDistance(g\g\mesh,ki\target) < dist
				ki\ttarget = g\mesh
				dist# = EntityDistance(ki\sh\piv,ki\ttarget)
			EndIf 
		Next
		
		dist# = EntityDistance(ki\sh\piv,ki\ttarget)
		dy# = DeltaYaw(ki\sh\piv,ki\ttarget)
		dp# = DeltaPitch(ki\sh\piv,ki\ttarget)
		
		dist# = EntityDistance(ki\sh\piv,ki\target)
		dy# = DeltaYaw(ki\sh\piv,ki\target)
		dp# = DeltaPitch(ki\sh\piv,ki\target)
		
		If dist>50 And Abs(dy)+Abs(dp)<60
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			If dist > 100 And Abs(dy)+Abs(dp)<10.0
				If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
					ki\sh\burnafter = 1
				EndIf
			Else
				ki\sh\burnafter = 0
			EndIf
		ElseIf dist<40 And ki\target = ki\ttarget
			If ki\sh\order = ORDER_MOVETO 
				ki\sh\order = ORDER_STOP
				ki\globaction = 0
				ki\target = 0
				FreeEntity ki\sh\opiv
				ki\sh\opiv = 0
			EndIf
			ki\globaction = 0
		EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		If Rand(0,10) = 5 And ki\sh\order <> ORDER_MOVETO
			tdist# = 301
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0
					dist# = EntityDistance(ki\sh\piv,zs\piv)/(zs\shc\size+(zs\shc\typ=4)*10) * ((zs\stealthed>0)*2+1)
					If dist <= 300 And dist < tdist
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=300 And thandle>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
			EndIf
		ElseIf Rand(0,10) = 1 And ki\globaction = 2
			For f.flag = Each flag
				If f\mesh = GetParent(ki\target) Then
					If f\team = ki\sh\team And f\takeper > 90 Then
						ki\globaction = 0
					EndIf
				EndIf
			Next
		EndIf
	End Select
End Function



Function KI_Scout(ki.kiplayer)
	Select ki\sh\order
	Case ORDER_ATTACK
		ki\tars = ki\sh\oship
		ki\target = ki\tars\piv
		ki\globaction = 1
	Case ORDER_MOVETO
		ki\target = ki\sh\opiv
		ki\globaction = 2
	End Select
	
	Select ki\globaction
	Case 0 ; wait / find
		If Rand(0,10) = 5
			tdist# = 601
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0
					dist# = EntityDistance(ki\sh\piv,zs\piv) * ((zs\stealthed>0)*2+1) * zs\shc\size
					If dist <= 900 And dist < tdist
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=900 And thandle>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
			Else
				tdist# = 30001
				thandle = -1
				For f.flag = Each flag
					dist = EntityDistance(ki\sh\piv,f\mesh)
					If (f\team <> ki\sh\team Or f\takeper<90) And tdist > dist And Rand(0,1)
						tdist = EntityDistance(ki\sh\piv,f\mesh)
						thandle = f\mesh
						For c.conquest = Each conquest
							
						Next
					EndIf
				Next
				If tdist <= 30000 And thandle <> -1
					ki\target		= CreatePivot(thandle)
					ki\globaction	= 2 ; flyto
				EndIf
			EndIf
			If ki\sh\shc\typ = 3
				Shi_Fire(ki\sh,3, Null)
			EndIf
		EndIf
	Case 1 ; attack
		dist# = EntityDistance(ki\sh\piv,ki\target)
		distt#	= dist# / (weaponid[ki\sh\weapgroup[1]]\speed#+ki\sh\frontspeed*1.1)
		PositionEntity ki_tpiv,ki\tars\x,ki\tars\y,ki\tars\z
		TranslateEntity ki_tpiv,ki\tars\dx*distt#,ki\tars\dy*distt#,ki\tars\dz*distt#,1
		dy# = DeltaYaw(ki\sh\piv,ki_tpiv)
		dp# = DeltaPitch(ki\sh\piv,ki_tpiv)
		
		If dist>920 And ki\sh\order <> ORDER_ATTACK
			ki\tars.ship	= Null
			ki\target		= 0
			ki\globaction	= 0
		ElseIf ki\tars\spawntimer>0
			ki\globaction = 0
		ElseIf dist/ki\sh\zzs < 1 And ki\sh\zzs > ki\tars\zzs
			ki\sh\zzs = ki\sh\zzs -ki\sh\shc\speeddown*main_gspe
		ElseIf dist>80 And Abs(dy)+Abs(dp)<40.0/(Abs(Float(ki\sh\zzs))+1)
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			If dist > 100 And Abs(dy)+Abs(dp)<10.0
				If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
					ki\sh\burnafter = 1
				EndIf
			Else
				ki\sh\burnafter = 0
			EndIf
		ElseIf dist/ki\tars\shc\size<40
			ki\globaction = 3 ; abdrehn 
		;ElseIf Abs(dy)+Abs(dp)>130/(Abs(Float(ki\sh\zzs))+1)
		;	If ki\sh\zzs>ki\sh\shc\lowspeed Then ki\sh\zzs = ki\sh\zzs-ki\sh\shc\speeddown*main_gspe
		EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		If Abs(dy)+Abs(dp)<10 And Rand(7)=4
			Shi_Fire(ki\sh,1,ki\tars)
			Shi_Fire(ki\sh,2,ki\tars)
			If ki\sh\shc\typ = 3
				Shi_Fire(ki\sh,3,ki\tars)
			EndIf
		EndIf
		If ki\sh\shc\typ = 3
			Shi_Fire(ki\sh,3,ki\tars)
		EndIf
	Case 2 ; fly to
		ki\ttarget = ki\target
		
		dist# = EntityDistance(ki\sh\piv,ki\ttarget)
		
		For g.gate = Each gate
			If EntityDistance(g\mesh,ki\sh\piv)+EntityDistance(g\g\mesh,ki\target) < dist
				ki\ttarget = g\mesh
				dist# = EntityDistance(ki\sh\piv,ki\ttarget)
			EndIf 
		Next
		
		dist# = EntityDistance(ki\sh\piv,ki\ttarget)
		dy# = DeltaYaw(ki\sh\piv,ki\ttarget)
		dp# = DeltaPitch(ki\sh\piv,ki\ttarget)
		
		dist# = EntityDistance(ki\sh\piv,ki\target)
		dy# = DeltaYaw(ki\sh\piv,ki\target)
		dp# = DeltaPitch(ki\sh\piv,ki\target)
		
		If dist>80 And Abs(dy)+Abs(dp)<60
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			
			If dist > 80 And Abs(dy)+Abs(dp)<10.0
				If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
					ki\sh\burnafter = 1
				EndIf
			Else
				ki\sh\burnafter = 0
			EndIf
			
		ElseIf dist<60 And ki\target = ki\ttarget
			If map_atmo = 0 
				ki\sh\zzs = ki\sh\zzs-ki\sh\shc\speeddown*main_gspe
				If ki\sh\zzs <= 0 Then ki\globaction = 0
			ElseIf Rand(25/main_gspe)=2
				ki\globaction = 0
			EndIf
			If ki\sh\order = ORDER_MOVETO 
				ki\sh\order = ORDER_STOP
				ki\globaction = 0
				ki\target = 0
				FreeEntity ki\sh\opiv
				ki\sh\opiv = 0
			EndIf
		EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		If Rand(0,10) = 5 And ki\sh\order <> ORDER_MOVETO
			tdist# = 401
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0
					dist# = EntityDistance(ki\sh\piv,zs\piv)
					If dist <= 400 And dist < tdist
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=400 And thandle>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
			EndIf
		ElseIf Rand(0,10) = 1 And ki\globaction = 2
			For f.flag = Each flag
				If f\mesh = GetParent(ki\target) Then
					If f\team = ki\sh\team And f\takeper > 90 Then
						ki\globaction = 0
					EndIf
				EndIf
			Next
		EndIf
	Case 3 ; abdrehn
		dist# = EntityDistance(ki\sh\piv,ki\target)
		TurnEntity ki\sh\piv,0,180,0
		dy# = DeltaYaw(ki\sh\piv,ki\target)
		dp# = DeltaPitch(ki\sh\piv,ki\target)
		TurnEntity ki\sh\piv,0,-180,0
		
		If dist*ki\tars\shc\size>150
			ki\globaction = 2
		Else
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			If dist < 200 And Abs(dy)+Abs(dp)<10.0
				If ki\sh\burnafter = 0 And ki\sh\afterburner > ki\sh\shc\afterburnertime / 2
					ki\sh\burnafter = 1
				EndIf
			Else
				ki\sh\burnafter = 0
			EndIf
		EndIf
		
		KI_TurnShip(ki, dp, dy)
	End Select
End Function



Function KI_Cargo(ki.kiplayer)
	Select ki\globaction
	Case 0 ; wait / find
		
	Case 1 ; attack

	Case 2 ; fly to
		dist# = EntityDistance(ki\sh\piv,ki\target)
		dy# = DeltaYaw(ki\sh\piv,ki\target)
		dp# = DeltaPitch(ki\sh\piv,ki\target)
		
		If dist>50 And Abs(dy)+Abs(dp)<70
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
		ElseIf dist<60
			ki\globaction = 0
		EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		Shi_Fire(ki\sh,3,Null)
	End Select
End Function



Function KI_BigShip(ki.kiplayer)
	Select ki\sh\order
	Case ORDER_ATTACK
		ki\tars = ki\sh\oship
		ki\target = ki\tars\piv
		ki\globaction = 1
	Case ORDER_MOVETO
		ki\target = ki\sh\opiv
		ki\globaction = 2
	End Select

	
	Select ki\globaction
	Case 0 ; wait / find
		If ki\sh\zzs>0 Then ki\sh\zzs = ki\sh\zzs-ki\sh\shc\speeddown*main_gspe
		If Rand(0,10) = 5
			tdist# = 2001
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0 And zs\shc\typ=3
					dist# = EntityDistanceB(ki\sh\piv,zs\piv,tdist+1+zs\hitpoints/1000+zs\shields/1000)
					If dist <= 2000 And dist < tdist
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=2000 And thandle>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
			Else
				tdist# = 30001
				thandle = -1
				For f.flag = Each flag
					dist = EntityDistanceB(ki\sh\piv,f\mesh,tdist)
					If (f\team <> ki\sh\team Or f\takeper<90) And tdist > dist And Rand(0,1)
						tdist = EntityDistance(ki\sh\piv,f\mesh)
						thandle = f\mesh
					EndIf
				Next
				If tdist <= 30000 And thandle <> -1
					ki\target		= CreatePivot(thandle)
					ki\globaction	= 2 ; flyto
				EndIf
			EndIf
			Shi_Fire(ki\sh,3,Null)	
		EndIf
	Case 1 ; attack
		dist# = EntityDistance(ki\sh\piv,ki\target)
		dy# = DeltaYaw(ki\sh\piv,ki\target)
		dp# = DeltaPitch(ki\sh\piv,ki\target)
		
		If dist>2100 And ki\sh\order <> ORDER_ATTACK
			ki\tars.ship	= Null
			ki\target		= 0
			ki\globaction	= 0
		ElseIf ki\tars\spawntimer>0
			ki\globaction = 0
		Else
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
			If dist < 150+5*(ki\sh\shc\size+ki\tars\shc\size) Then dy = -Sgn(dy)*(180-Abs(dy)) : dp = -Sgn(dp)*(90-Abs(dp))
		EndIf
		;ElseIf dist>15*(ki\sh\shc\size+ki\tars\shc\size) And Abs(dy)+Abs(dp)<70
		;	If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
		;ElseIf dist<15*(ki\sh\shc\size+ki\tars\shc\size) Or Abs(dy)+Abs(dp)>160
		;	If ki\sh\zzs>ki\sh\shc\lowspeed Then ki\sh\zzs = ki\sh\zzs-ki\sh\shc\speeddown*main_gspe
		;EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		If Abs(dy)+Abs(dp)<5 And Rand(7)=4
			Shi_Fire(ki\sh,1,ki\tars)
			Shi_Fire(ki\sh,2,ki\tars)
		EndIf
		Shi_Fire(ki\sh,3,ki\tars)
	Case 2 ; fly to
		dist# = EntityDistance(ki\sh\piv,ki\target)
		dy# = DeltaYaw(ki\sh\piv,ki\target)
		dp# = DeltaPitch(ki\sh\piv,ki\target)
		
		If dist>70 And Abs(dy)+Abs(dp)<70
			If ki\sh\zzs<ki\sh\shc\topspeed Then ki\sh\zzs = ki\sh\zzs+ki\sh\shc\speedup*main_gspe
		ElseIf dist<80
			ki\globaction = 0
			If ki\sh\order = ORDER_MOVETO 
				ki\sh\order = ORDER_STOP
				ki\globaction = 0
				ki\target = 0
				FreeEntity ki\sh\opiv
				ki\sh\opiv = 0
			EndIf
		EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		Shi_Fire(ki\sh,3,Null)
		
		If Rand(0,10) = 5 And ki\sh\order <> ORDER_MOVETO
			tdist# = 2001
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0 And zs\shc\typ=3
					dist# = EntityDistanceB(ki\sh\piv,zs\piv,tdist+zs\hitpoints/1000+zs\shields/1000)
					If dist <= 2000 And dist < tdist
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=2000 And thandle>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
			EndIf
		ElseIf Rand(0,10) = 1 And ki\globaction = 2
			For f.flag = Each flag
				If f\mesh = GetParent(ki\target) Then
					If f\team = ki\sh\team And f\takeper > 90 Then
						ki\globaction = 0
					EndIf
				EndIf
			Next
		EndIf
	End Select
End Function

Function KI_Cannon(ki.kiplayer)
	Select ki\sh\order
	Case ORDER_ATTACK
		ki\tars = ki\sh\oship
		ki\target = ki\tars\piv
		ki\globaction = 1
	End Select
	
	Select ki\globaction
	Case 0 ; wait / find
		If ki\sh\zzs>0 Then ki\sh\zzs = ki\sh\zzs-ki\sh\shc\speeddown*main_gspe
		If Rand(0,10) = 5
			tdist# = weaponid[ki\sh\weapgroup[weapsigi(ki\sh\shc\weapsig,ki\sh\shc\mainweap,1)]]\range+1
			thandle = -1
			For zs.ship = Each ship
				If zs\team <> ki\sh\team And Rand(0,1) And zs\spawntimer <= 0
					dist1# = EntityDistance(ki\sh\piv,zs\piv)
					dist# = dist1/4 + dist1 / zs\shc\size * 2
					If dist < tdist And dist1 < weaponid[ki\sh\weapgroup[weapsigi(ki\sh\shc\weapsig,ki\sh\shc\mainweap,1)]]\range
						tdist = dist
						thandle = Handle(zs)
					EndIf
				EndIf
			Next
			If tdist<=weaponid[ki\sh\weapgroup[weapsigi(ki\sh\shc\weapsig,ki\sh\shc\mainweap,1)]]\range+1 And thandle>-1
				ki\tars.ship	= Object.ship(thandle)
				ki\target		= ki\tars\piv
				ki\globaction	= 1 ; attack
			EndIf
			Shi_Fire(ki\sh,3,Null)	
		EndIf
	Case 1 ; attack
		dist# = EntityDistance(ki\sh\piv,ki\target)
		distt#	= dist# / (weaponid[ki\sh\weapgroup[1]]\speed#+ki\sh\frontspeed*1.1)
		PositionEntity ki_tpiv,ki\tars\x,ki\tars\y,ki\tars\z
		TranslateEntity ki_tpiv,ki\tars\dx*distt#,ki\tars\dy*distt#,ki\tars\dz*distt#,1
		dy# = DeltaYaw(ki\sh\piv,ki_tpiv)
		dp# = DeltaPitch(ki\sh\piv,ki_tpiv)
		
		If dist/ ki\tars\shc\size>weaponid[ki\sh\weapgroup[weapsigi(ki\sh\shc\weapsig,ki\sh\shc\mainweap,1)]]\range
			ki\tars.ship	= Null
			ki\target		= 0
			ki\globaction	= 0
		ElseIf ki\tars\spawntimer>0
			ki\globaction = 0
		EndIf
		
		KI_TurnShip(ki, dp, dy)
		
		If Abs(dy)+Abs(dp)<10 And Rand(7)=4
			Shi_Fire(ki\sh,1,ki\tars)
			Shi_Fire(ki\sh,2,ki\tars)
		EndIf
		Shi_Fire(ki\sh,3,ki\tars)
	End Select
End Function

Function KI_GetKI.kiplayer(s.ship)
	For ki.kiplayer = Each kiplayer
		If ki\sh = s Then Return ki
	Next
End Function

Function KI_Reset()
	For ki.kiplayer = Each kiplayer
		ki\tars.ship	= Null
		ki\target		= 0
		ki\globaction	= 0
	Next
End Function


Function KI_Clear()
	If ki_tpiv <> 0 Then FreeEntity ki_tpiv
	ki_tpiv = 0
	For ki.kiplayer = Each kiplayer
		Delete ki.kiplayer
	Next
End Function