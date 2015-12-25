Type weapon
	Field class
	Field Typ
	Field name$
	Field mesh, radius
	Field sound,hitsound
	Field hitcolorR, hitcolorG, hitcolorB
	Field speed#,sspeed#, range
	Field livetime
	Field rndturn#
	Field actrange
	Field target
	Field targetspeed#
	Field tturnspeed#
	Field sdamage,hdamage
	Field shockwave#
	Field emp
	Field spawnshipclass
	Field swarm
	Field fxsig$
	Field muzzle
	Field neammo, reammo
	Field nepower
	Field reload
	Field aiming
	Field trail, trailsize#
	Field base1, base2, gun
	Field gunoffsety#, gunoffsetz#
	Field defaultpitch#, minpitch#
End Type

Global weaponid.weapon[50]
Global wea_count=0

Const WEA_SHOT		= 1
Const WEA_ROCKET	= 2
Const WEA_HEAVY		= 3
Const WEA_FLAK 		= 4
Const WEA_RAY 		= 5
Const WEA_SPAWNER	= 6


Global wea_sigcount = 0
Dim weapsigf#(2,2,4)
Dim weapsigi(2,2,2)

Type shoot
	Field id
	Field x#,y#,z#
	Field pis#, yas#
	Field P#,ya#,r#
	Field crtime#
	Field mesh
	Field speed#
	Field tar.ship
	Field swc.weapon
	Field par.ship ; starter
End Type

Type shockwave
	Field x#,y#,z#
	Field power#
	Field size#
	Field i#
End Type

Function Wea_LoadWeapons()
	dir = ReadDir(datad+"WEAPONS/WEAPS")
	NextFile(dir)
	NextFile(dir)
	i = 0
	wea_count = 0
	Repeat
		pfad$ = NextFile(dir)
		If pfad$="" Then Exit
		If Right(pfad$,3) = "ini" Then Wea_LoadWeapon(datad+"WEAPONS/WEAPS/"+pfad$)
		i = i + 1
	Until pfad$=""
	CloseDir dir
	
	dir = ReadDir(datad+"WEAPONS/SIGS")
	NextFile(dir)
	NextFile(dir)
	i = 0
	Repeat
		pfad$ = NextFile(dir)
		If pfad$="" Then Exit
		i = i + 1
	Until pfad$=""
	CloseDir dir

	Dim weapsigf#(25,10,4)
	Dim weapsigi(25,10,1)
	dir = ReadDir(datad+"WEAPONS/SIGS")
	NextFile(dir)
	NextFile(dir)
	i = 0
	Repeat
		pfad$ = NextFile(dir)
		If pfad$="" Then Exit
		If Right(pfad$,3) = "ini" Then Wea_LoadWeaponSig(datad+"WEAPONS/SIGS/"+pfad$)
		i = i + 1
	Until pfad$=""
	CloseDir dir
End Function

Function Wea_LoadWeapon(pfad$)
	w.weapon = New weapon
	Util_CheckFile(pfad$)
	stream = ReadFile(pfad$)
	w\swarm = 1
	w\aiming = 10
	w\trailsize = 1
	w\reammo = 1
	w\radius = 2
	Repeat
		lin$ = ReadLine(stream)
		If Util_GetParas(lin$)
			Select paras[0]
			Case "Class"
				w\class 	= paras[1]
			Case "Type"
				w\typ		= paras[1]
			Case "name",main_lang+"name"
				w\name$		= paras[1]
			Case "Mesh"
				w\mesh		= LoadMeshA(paras[1])
				HideEntity w\mesh
			Case "Radius"
				w\radius	= paras[1]
			Case "MeshBlend"
				EntityBlend w\mesh,paras[1]
			Case "MeshFX"
				EntityFX w\mesh,paras[1]
			Case "Sound"
				w\sound		= Load3DSound(paras[1])
			Case "HitSound"
				w\hitsound	= Load3DSound(paras[1])
			Case "Speed"
				w\speed# 	= paras[1]
			Case "SSpeed"
				w\sspeed	= paras[1]
			Case "Livetime"
				w\livetime	= paras[1]
			Case "RndTurn"
				w\rndturn#	= paras[1]
			Case "ActivationRange"
				w\actrange= paras[1]
			Case "Target"
				w\target	= paras[1]
			Case "TargetSpeed"
				w\targetspeed=paras[1]
			Case "TTurnSpeed"
				w\tturnspeed= paras[1]
			Case "Aiming"
				w\aiming	= paras[1]
			Case "ShieldDamage"
				w\sdamage	= paras[1]
			Case "HullDamage"
				w\hdamage	= paras[1]
			Case "Shockwave"
				w\shockwave	= paras[1]
			Case "EMP"
				w\emp		= paras[1]
			Case "SpawnShipClass"
				w\spawnshipclass = paras[1]
			Case "Swarm"
				w\swarm		= paras[1]
			Case "FXSig"
				w\fxsig		= paras[1]
			Case "Muzzle"
				w\muzzle	= LoadMeshA(paras[1])
				HideEntity w\muzzle
			Case "NeededAmmo"
				w\neammo	= paras[1]
			Case "ReAmmo"
				w\reammo	= paras[1]
			Case "NeededPower"
				w\nepower	= paras[1]
			Case "Reload"
				w\reload	= paras[1]
			Case "Trail"
				w\trail		= paras[1]
			Case "TrailSize"
				w\trailsize		= paras[1]
			Case "Base1"
				w\base1		= LoadMeshA(paras[1])
				HideEntity w\base1
			Case "Base2"
				w\base2		= LoadMeshA(paras[1])
				HideEntity w\base2
			Case "Gun"
				w\gun		= LoadMeshA(paras[1])
				HideEntity w\gun
			Case "GunOffsetY"
				w\gunoffsety= paras[1]
			Case "GunOffsetZ"
				w\gunoffsetz= paras[1]
			Case "DefaultPitch"
				w\defaultpitch= paras[1]
			Case "MinPitch"
				w\minpitch	= paras[1]
			Case "HitColorRed"
				w\hitcolorR	= paras[1]
			Case "HitColorGreen"
				w\hitcolorG	= paras[1]
			Case "HitColorBlue"
				w\hitcolorB	= paras[1]
			End Select
		EndIf
	Until Eof(stream)
	If w\fxsig<>"" Then FX_LoadFXSig(w\fxsig,w\mesh,Null)
	w\range = w\speed * w\livetime
	class = w\class
	weaponid[class] = w 
End Function

Function Wea_LoadWeaponSig(Pfad$)
	Util_CheckFile(pfad$)
	stream = ReadFile(pfad$)
	Repeat
		lin$ = ReadLine(stream)
		If Util_GetParas(lin$)
			Select paras[0]
			Case "SigID"
				id = paras[1]
			Default
				For i = 1 To 10
					Select paras[0]
					Case "Slot"+i+"X"
						weapsigf#(id,i,0) = paras[1]
					Case "Slot"+i+"Y"
						weapsigf#(id,i,1) = paras[1] 
					Case "Slot"+i+"Z"
						weapsigf#(id,i,2) = paras[1]
					Case "Slot"+i+"Pitch"
						weapsigf#(id,i,3) = paras[1] 
					Case "Slot"+i+"Yaw"
						weapsigf#(id,i,4) = paras[1]
					Case "Slot"+i+"Type"
						weapsigi(id,i,0) = paras[1]
					Case "Slot"+i+"Group"
						weapsigi(id,i,1) = paras[1] 
					End Select
				Next
			End Select
		EndIf
	Until Eof(stream)
	If wea_sigcount <= id Then wea_sigcount = wea_sigcount + 1
End Function

Function Wea_LoadWeaponSigForShip(stream)
	id = wea_sigcount
	wea_sigcount = wea_sigcount + 1
	Repeat
		lin$ = ReadLine(stream)
		For i = 1 To 10
			Select Lower(Trim(lin))
			Case "weapon"+i+"{"
				Repeat
					ilin$ = ReadLine(stream)
					If Util_GetParas(ilin$)
						Select paras[0]
						Case "x"
							weapsigf#(id,i,0) = paras[1]
						Case "y"
							weapsigf#(id,i,1) = paras[1] 
						Case "z"
							weapsigf#(id,i,2) = paras[1]
						Case "pitch"
							weapsigf#(id,i,3) = paras[1] 
						Case "yaw"
							weapsigf#(id,i,4) = paras[1]
						Case "type"
							weapsigi(id,i,0) = paras[1]
						Case "slot"
							weapsigi(id,i,1) = paras[1] 
						End Select
					EndIf
				Until Trim(ilin) = "}" Or Eof(stream)
			End Select
		Next
	Until Trim(lin) = "}" Or Eof(stream)
	Return id
End Function

Function Wea_CreateShoot(x#,y#,z#,p#,ya#,r#,class,target=0,par.ship,dist# = 0,id=-1,turret=0)
	For i = 1 To weaponid[class]\swarm
		sho.shoot	= New shoot
		If id = -1 Then
			id = wea_count
			wea_count = (wea_count + 1) Mod 65000
			sho\id	= id
		Else
			sho\id	= id + i - 1
		EndIf
		sho\x#		= x#
		sho\y#		= y#
		sho\z#		= z#
		sho\swc		= weaponid[class]
		sho\p#		= p#+Rnd(-sho\swc\rndturn,sho\swc\rndturn)
		sho\ya#		= ya#+Rnd(-sho\swc\rndturn,sho\swc\rndturn)
		sho\r#		= r#
		sho\mesh	= CopyEntity(weaponid[class]\mesh)
		sho\par		= par
		If turret Then
			sho\speed	= sho\swc\sspeed
		Else
			sho\speed	= sho\swc\sspeed + sho\par\frontspeed * 1.2
		EndIf
		
		sho\crtime#	= sho\swc\livetime
		
		PositionEntity sho\mesh,sho\x,sho\y,sho\z
		RotateEntity sho\mesh,sho\p,sho\ya,sho\r
		
		If sho\swc\typ = 5
			EntityPickMode sho\mesh,1
			EntityParent sho\mesh,sho\par\mesh
		ElseIf turret=0
			MoveEntity sho\mesh,0,0,sho\speed
		EndIf
		
		If target Then 
			If par<>Null
				If par\targeting >= 100 Or turret = 1 Then sho\tar.ship = Object.ship(target)
			Else
				sho\tar.ship = Object.ship(target)
			EndIf
			If sho\swc\typ=4
				sho\crtime = dist/sho\speed+Rand(-1,9);EntityDistance(sho\tar\piv,sho\mesh)/sho\speed+Rand(-5,5)
			EndIf
		EndIf
		
		If sho\swc\typ <> 5 And (net_isserver=1 Or net = 0)
			EntityType sho\mesh,wea_colli
			EntityRadius sho\mesh,sho\swc\radius
			ResetEntity sho\mesh
		EndIf
		
		If sho\swc\trail>0
			Trail_Assign(sho\mesh,sho\mesh,sho\swc\trail,255,255,255,sho\swc\trailsize,sho\speed)
		EndIf
	Next
	If EntityInBoxDistance(sho\mesh,cc_cam,1100)
		;EmitSound(sho\swc\sound,sho\mesh)
		If sho\swc\muzzle <> 0 Then
			muzzle = CopyEntity(sho\swc\muzzle)
			PositionEntity muzzle,x,y,z
			RotateEntity muzzle,sho\p,sho\ya,sho\r
			EntityParent muzzle,par\piv
			FX_CreateShipFX(FX_FadeOut,par, muzzle, 1,1000.0/sho\swc\hdamage+5)
		EndIf
		SoundPitch sho\swc\sound,Rand(8000,30000)
		FX_Fake3dSound(sho\swc\sound,x#,y#,z#,.9)
	EndIf
	
	Return Handle(sho)
End Function

Function Wea_UpdateShoots()
	tr# = .8 ^ main_gspe
	For sho.shoot = Each shoot
		sho\crtime# = sho\crtime# - 1*main_gspe
		;EntityAlpha sho\mesh,0
		Select sho\swc\typ
		Case 5
			sho\speed = sho\speed + (sho\speed<sho\swc\speed)*10*main_gspe
			If sho\speed > 1000 Then sho\speed = 1000
			sholen# = sho\speed
			
			If sho\tar <> Null And sho\swc\tturnspeed<>0 Then
				TurnEntity sho\mesh,sho\swc\tturnspeed*Sgn(DeltaPitch(sho\mesh,sho\tar\piv))*main_gspe,sho\swc\tturnspeed*Sgn(DeltaYaw(sho\mesh,sho\tar\piv))*main_gspe,0
			EndIf
			
			ScaleEntity sho\mesh,1,1,1
			tcoll = EntityPick(sho\mesh,sho\speed*4)
			If tcoll
				x#	= PickedX()
				y#	= PickedY()
				z#	= PickedZ()
				sholen# = Util_CoordinateDistance(x,y,z,EntityX(sho\mesh,1),EntityY(sho\mesh,1),EntityZ(sho\mesh,1))/10
				s.ship = Object.ship(Shi_FindByMesh(tcoll))
				If s <> Null Then 
					If sho\par <> Null And (net_isserver=1 Or net = 0)
						If s\team <> sho\par\team Then s\shields	= s\shields - sho\swc\sdamage * main_gspe
					EndIf
					
					dist = EntityDistance(s\piv,sho\mesh)
										
					If s\shc\fixed = 0
						s\dx = s\dx - ((s\x-EntityX(sho\mesh))/dist) / 15.0 / s\shc\size^2 * main_gspe
						s\dy = s\dy - ((s\y-EntityY(sho\mesh))/dist) / 15.0 / s\shc\size^2 * main_gspe
						s\dz = s\dz - ((s\z-EntityZ(sho\mesh))/dist) / 15.0 / s\shc\size^2 * main_gspe
					EndIf
					
					If s = main_pl Then cc_quake = cc_quake + .1
					
					If s\shields < 0 Then s\shields = 0
					ts#			= s\shields
					If ts# < 51 Then
						If ts# < 1 Then ts# = 1
						
						If s\shc\size > 7 And Rand(6000)<sho\swc\hdamage Then
							TFormPoint x,y,z, 0,s\mesh
							Shi_HitShip(s, TFormedX(),TFormedY(),TFormedZ(), sho\swc\hdamage/5.0)
						EndIf
						
						If EntityInView(sho\mesh,cc_cam) And Rand(0,4)=1
							If sho\swc\hitcolorR + sho\swc\hitcolorG + sho\swc\hitcolorB = 0 Then 
								sx#	= sho\swc\hdamage / Rnd(50,55) + 1
								sy# = sho\swc\hdamage / Rnd(50,55) + 1
								FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(230,255),Rnd(99,160),Rnd(60),Rand(50,99))
								sx# = sx * Rnd(.6,1)
								sy# = sy * Rnd(.6,1)
								FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(50,99))
							Else
								sx#	= sho\swc\hdamage / Rnd(50,55) + 1
								sy# = sho\swc\hdamage / Rnd(50,55) + 1
								FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50) ,Rand(50,99))
								sx# = sx * Rnd(.6,1)
								sy# = sy * Rnd(.6,1)
								FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50), Rand(50,99))
							EndIf
							
							FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,sho\swc\hdamage/100)
						EndIf
					ElseIf sho\par<>Null
						If s\team <> sho\par\team And Rand(15)=10 Then FX_ShieldFX(s, x#,y#,z#, sho\swc\sdamage/100.0+1)
					EndIf
					If sho\par <> Null And (net_isserver=1 Or net = 0)
						oh = s\hitpoints
						If s\team <> sho\par\team Then s\hitpoints = s\hitpoints - (sho\swc\hdamage / ts# / 1.25 + sho\swc\hdamage / 5) * main_gspe
						If oh > 0 And s\hitpoints < 0 Then s\hitby = sho\par\id
						If net_update Then Wea_SendHit(sho,s)
					EndIf 
					
				Else
					For w.wreck = Each wreck
						If w\mesh = tcoll Then w\hitpoints = w\hitpoints - sho\swc\hdamage
					Next
					
					If EntityInView(sho\mesh,cc_cam) And Rand(0,4)=1
						If sho\swc\hitcolorR + sho\swc\hitcolorG + sho\swc\hitcolorB = 0 Then 
							sx#	= sho\swc\hdamage / Rnd(50,55) + 1
							sy# = sho\swc\hdamage / Rnd(50,55) + 1
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(230,255),Rnd(99,160),Rnd(60),Rand(50,99))
							sx# = sx * Rnd(.6,1)
							sy# = sy * Rnd(.6,1)
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(50,99))
						Else
							sx#	= sho\swc\hdamage / Rnd(50,55) + 1
							sy# = sho\swc\hdamage / Rnd(50,55) + 1
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50) ,Rand(50,99))
							sx# = sx * Rnd(.6,1)
							sy# = sy * Rnd(.6,1)
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50), Rand(50,99))
						EndIf
						FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,sho\swc\hdamage/100)
					EndIf
				EndIf
			EndIf
			
			ScaleEntity sho\mesh,sho\speed/400*Rnd(.9,1.2),sho\speed/400*Rnd(.9,1.2),sholen
			
			If sho\crtime<=0 Then
				EntityType sho\mesh,0
				FreeEntity sho\mesh
				Delete sho.shoot
			EndIf
		Default
			MoveEntity sho\mesh,0,0,sho\speed#*main_gspe
			
			If Main_CriticalFPS > 0.5 Then
				TFormPoint 0,0,0,sho\mesh, cc_cam
				If TFormedZ()>-20 And TFormedZ()<3000 / Main_CriticalFPS Then
					EntityAlpha sho\mesh,1
				Else
					EntityAlpha sho\mesh,0
				EndIf
			EndIf
			
			If sho\tar <> Null And sho\swc\tturnspeed<>0 Then
				distt#	= EntityDistance(sho\tar\piv, sho\mesh) / sho\speed
				PositionEntity ki_tpiv,sho\tar\x,sho\tar\y,sho\tar\z
				TranslateEntity ki_tpiv,sho\tar\dx*distt#,sho\tar\dy*distt#,sho\tar\dz*distt#,1
				dy# = DeltaYaw(sho\mesh,ki_tpiv)
				dp# = DeltaPitch(sho\mesh,ki_tpiv)
				
				sho\pis = sho\pis*tr + sho\swc\tturnspeed*Sgn(dp)*main_gspe/4.0
				sho\yas = sho\yas*tr + sho\swc\tturnspeed*Sgn(dy)*main_gspe/4.0
				
				TurnEntity sho\mesh,sho\pis,sho\yas,0
				dist = EntityDistance(sho\mesh,sho\tar\piv)
				If dist < sho\tar\indanger Then sho\tar\indanger = dist
				
				If sho\tar\spawntimer > 0 Then sho\tar = Null
			ElseIf sho\swc\actrange <> 0 And sho\par <> Null Then
				dist = sho\swc\actrange
				For i = 0 To Ceil(main_gspe)
					If Abs(sho\speed-sho\swc\sspeed)>0.01 Then sho\speed = sho\speed * .9 + sho\swc\sspeed *.1
				Next
				If Rand(3)=2 And (net_isserver=1 Or net = 0)
					For s.ship = Each ship
						If s\team <> sho\par\team Then
							distt# = EntityDistance(sho\mesh,s\piv) - s\shc\size*2
							If distt < dist Then
								dist = distt
								sho\tar = s
							EndIf
						EndIf
					Next
					If sho\tar <> Null And net_isserver = 1 Then Wea_SendTarget(sho)
				EndIf
			EndIf
			
			If sho\speed < sho\swc\speed And sho\tar <> Null Then sho\speed = sho\speed - (sho\speed-sho\swc\speed)/4
			
			tcoll = EntityCollided(sho\mesh,shi_colli2)
			If tcoll = 0 Then tcoll = EntityCollided(sho\mesh,shi_collibig)
			If tcoll Then
				s.ship = Object.ship(Shi_FindByMesh(tcoll))
				If Not (sho\par = s And sho\crtime > sho\swc\livetime-4*main_gspe)
					If s <> Null And (net_isserver = 1 Or net = 0 ) Then
						If sho\par <> Null
							If s\team <> sho\par\team Then s\shields	= s\shields - sho\swc\sdamage
						EndIf
						If s\shields < 0 Then s\shields = 0
						ts#			= s\shields
						If ts# < 51 Then
							If ts# < 1 Then ts# = 1
							
							If s\shc\size > 7 And Rand(4000)<sho\swc\hdamage Then
								TFormPoint CollisionX(sho\mesh,1),CollisionY(sho\mesh,1), CollisionZ(sho\mesh,1), 0,s\mesh
								Shi_HitShip(s, TFormedX(),TFormedY(),TFormedZ(), sho\swc\hdamage/190.0)
							EndIf
							
							x#	= CollisionX(sho\mesh,1)
							y#	= CollisionY(sho\mesh,1)
							z#	= CollisionZ(sho\mesh,1)
							
							If EntityInView(sho\mesh,cc_cam)
								If sho\swc\hitcolorR + sho\swc\hitcolorG + sho\swc\hitcolorB = 0 Then 
									sx#	= sho\swc\hdamage / Rnd(50,55) + 1
									sy# = sho\swc\hdamage / Rnd(50,55) + 1
									FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(230,255),Rnd(99,160),Rnd(60),Rand(50,99))
									sx# = sx * Rnd(.6,1)
									sy# = sy * Rnd(.6,1)
									FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(50,99))
								Else
									sx#	= sho\swc\hdamage / Rnd(50,55) + 1
									sy# = sho\swc\hdamage / Rnd(50,55) + 1
									FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50) ,Rand(50,99))
									sx# = sx * Rnd(.6,1)
									sy# = sy * Rnd(.6,1)
									FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50), Rand(50,99))
								EndIf
							EndIf
							If sho\swc\shockwave
								Wea_CreateWave(x,y,z,sho\swc\hdamage,sho\swc\shockwave^100)
								If sho\swc\shockwave > 1.035
									fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
									fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
								EndIf
							EndIf
							FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,sho\swc\hdamage/100.0+.1)
						ElseIf sho\par<>Null
							x#	= CollisionX(sho\mesh,1)
							y#	= CollisionY(sho\mesh,1)
							z#	= CollisionZ(sho\mesh,1)
							If s\team <> sho\par\team Then s\shieldvis = 50 : FX_ShieldFX(s, x#,y#,z#, sho\swc\sdamage/100.0+1) : EmitSound(shi_shieldsfx,s\piv)
						EndIf
						If sho\par <> Null
							oh = s\hitpoints
							If s\team <> sho\par\team Then s\hitpoints = s\hitpoints - sho\swc\hdamage / ts# / 1.25 - sho\swc\hdamage / 5
							If oh > 0 And s\hitpoints < 0 Then s\hitby = sho\par\id
							If s = main_pl And s\team <> sho\par\team Then cc_quake = cc_quake + (.1 + sho\swc\hdamage / 150.0) / Float(s\shc\size)
						EndIf 
						Wea_SendHit(sho,s)
					EndIf
					sho\crtime = -1
				Else
					ResetEntity sho\mesh
				EndIf
			ElseIf EntityCollided(sho\mesh,map_colli)
				tcoll = EntityCollided(sho\mesh,map_colli)
				x#	= CollisionX(sho\mesh,1)
				y#	= CollisionY(sho\mesh,1)
				z#	= CollisionZ(sho\mesh,1)
				If EntityInView(sho\mesh,cc_cam)
					If sho\swc\hitcolorR + sho\swc\hitcolorG + sho\swc\hitcolorB = 0 Then 
						sx#	= sho\swc\hdamage / Rnd(50,55) + 1
						sy# = sho\swc\hdamage / Rnd(50,55) + 1
						FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(230,255),Rnd(99,160),Rnd(60),Rand(50,99))
						sx# = sx * Rnd(.6,1)
						sy# = sy * Rnd(.6,1)
						FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(50,99))
					Else
						sx#	= sho\swc\hdamage / Rnd(50,55) + 1
						sy# = sho\swc\hdamage / Rnd(50,55) + 1
						FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50) ,Rand(50,99))
						sx# = sx * Rnd(.6,1)
						sy# = sy * Rnd(.6,1)
						FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50), Rand(50,99))
					EndIf
				EndIf
				If sho\swc\shockwave
					Wea_CreateWave(x,y,z,sho\swc\hdamage,sho\swc\shockwave^100)
					If sho\swc\shockwave > 1.035
						fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
						fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
					EndIf
				EndIf
				For w.wreck = Each wreck
					If w\mesh = tcoll Then w\hitpoints = w\hitpoints - sho\swc\hdamage
				Next
				FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,sho\swc\hdamage/100.0+.1)
				sho\crtime = -1
				Wea_SendHit(sho,Null)
			ElseIf EntityCollided(sho\mesh,map_forcefield)
				x#	= CollisionX(sho\mesh,1)
				y#	= CollisionY(sho\mesh,1)
				z#	= CollisionZ(sho\mesh,1)
				fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),1.04,1)
				fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),1.04,1)
				sho\crtime = -1
				Wea_SendHit(sho,Null)
			Else
				For su.wat_surf = Each wat_surf
					If EntityY(su\piv,1)>EntityY(sho\mesh,1)
						x#	= EntityX(sho\mesh,1)
						y#	= EntityY(sho\mesh,1)
						z#	= EntityZ(sho\mesh,1)
						If EntityInView(sho\mesh,cc_cam)
							sx#	= sho\swc\hdamage / Rnd(50,55) + 1
							sy# = sho\swc\hdamage / Rnd(50,55) + 1
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(230,255),Rnd(99,160),Rnd(60),Rand(50,99))
							sx# = sx * Rnd(.6,1)
							sy# = sy * Rnd(.6,1)
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(50,99))
							;EmitSound sho\swc\hitsound,ex
						EndIf
						If sho\swc\shockwave
							fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
							fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
						EndIf
						sho\crtime = -1
						Exit
					EndIf
				Next
			EndIf
			sho\x		= EntityX(sho\mesh)
			sho\y		= EntityY(sho\mesh)
			sho\z		= EntityZ(sho\mesh)
			If sho\crtime<=0 Then
				If sho\swc\target = 1
					x#	= sho\x#
					y#	= sho\y#
					z#	= sho\z#
					FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,sho\swc\hdamage/200.0+.1)
					If EntityInView(sho\mesh,cc_cam)
						If sho\swc\hitcolorR + sho\swc\hitcolorG + sho\swc\hitcolorB = 0 Then 
							sx#	= sho\swc\hdamage / Rnd(50,55) + 1
							sy# = sho\swc\hdamage / Rnd(50,55) + 1
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(230,255),Rnd(99,160),Rnd(60),Rand(50,99))
							sx# = sx * Rnd(.6,1)
							sy# = sy * Rnd(.6,1)
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(50,99))
						Else
							sx#	= sho\swc\hdamage / Rnd(50,55) + 1
							sy# = sho\swc\hdamage / Rnd(50,55) + 1
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50) ,Rand(50,99))
							sx# = sx * Rnd(.6,1)
							sy# = sy * Rnd(.6,1)
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,sho\swc\hitcolorR + Rnd(0,50),sho\swc\hitcolorG + Rnd(0,50), sho\swc\hitcolorB + Rnd(0,50), Rand(50,99))
						EndIf
						
						If sho\swc\shockwave
							Wea_CreateWave(x,y,z,sho\swc\hdamage,sho\swc\shockwave^100)
							If sho\swc\shockwave > 1.035
								fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
								fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
							EndIf
						EndIf
					EndIf
				EndIf
				If sho\swc\typ = WEA_SPAWNER And sho\par <> Null
					c.class = Race_GetClassByID(sho\swc\spawnshipclass,sho\par\team,3)
					s.ship = Object.ship(Shi_CreateShip(sho\x,sho\y,sho\z,sho\swc\spawnshipclass,c\name,sho\par\team,2,3))
					s\team	 = sho\par\team
					s\selclass = sho\swc\spawnshipclass
					s\selspawn = 255
					s\spawntimer = 5
					Shi_SendSpawnData(s)
				EndIf
				
				If sho\swc\emp <> 0 Then
					For sho2.shoot = Each shoot
						If sho2 <> sho Then
							If EntityDistance(sho2\mesh, sho\mesh) < sho\swc\emp
								sho2\crtime = 0
							EndIf
						EndIf
					Next
					If sho\swc\shockwave
						x#	= sho\x#
						y#	= sho\y#
						z#	= sho\z#
						fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
						fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
						fx_createexplosion3(x,y,z,2,2,355,5,5,Rand(100,150),sho\swc\shockwave,1)
					EndIf
				EndIf
				
				EntityType sho\mesh,0
				FreeEntity sho\mesh
				Trail_Remove(sho\mesh)
				Delete sho.shoot
			EndIf
		End Select
	Next
End Function

Function Wea_SendHit(sho.shoot,s.ship) ; 6 byte
	If net_isserver
		AddUDPByte(C_Hit)
		AddUDPShort(sho\id)
		If s <> Null
			AddUDPByte(s\id)
			AddUDPByte(util_minmax(s\shields*250/s\shc\shields,0,250))
			AddUDPByte(util_minmax(s\hitpoints*250/s\shc\hitpoints,0,250))
		Else
			AddUDPByte(255)
			AddUDPByte(255)
			AddUDPByte(255)
		EndIf
	EndIf
End Function

Function Wea_GetHit()
	wid = ReadUDPShort()
	sid = ReadUDPByte()
	shields = ReadUDPByte()
	hps		= ReadUDPByte()
	
	For sho.shoot = Each shoot
		If sho\id = wid
			If sid <> 255
				s.ship	= ships.ship(sid)
				If s <> Null
					oh = s\hitpoints
					s\shields = shields * s\shc\shields / 250
					s\hitpoints = hps * s\shc\hitpoints / 250
					If oh > 0 And s\hitpoints < 0 Then s\hitby = sho\par\id
					ts#			= s\shields
					If ts# < 51 Then
						If ts# < 1 Then ts# = 1
						x#	= EntityX(sho\mesh)
						y#	= EntityY(sho\mesh)
						z#	= EntityZ(sho\mesh)
						If s\shc\size > 7 And Rand(4000)<sho\swc\hdamage Then
							TFormPoint x,y,z, 0,s\mesh
							Shi_HitShip(s, TFormedX(),TFormedY(),TFormedZ(), sho\swc\hdamage/190.0)
						EndIf
						
						If EntityInView(sho\mesh,cc_cam)
							sx#	= sho\swc\hdamage / Rnd(50,55)+1
							sy# = sho\swc\hdamage / Rnd(50,55)+1
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(230,255),Rnd(99,160),Rnd(60),Rand(50,99))
							sx# = sx * Rnd(.6,1)
							sy# = sy * Rnd(.6,1)
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(50,99))
						EndIf
						If sho\swc\shockwave
							Wea_CreateWave(x,y,z,sho\swc\hdamage,sho\swc\shockwave^100)
							If sho\swc\shockwave > 1.035
								fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
								fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
							EndIf
						EndIf
					ElseIf sho\par<>Null
						If s\team <> sho\par\team Then s\shieldvis = 50 FX_ShieldFX(s, x#,y#,z#, sho\swc\sdamage/100.0+1)
					EndIf
				EndIf
			EndIf
			If sho\swc\typ <> 5
				sho\crtime = -1
				If sho\crtime<=0 Then
					If sho\swc\target = 1
						If EntityInView(sho\mesh,cc_cam)
							x#	= sho\x#
							y#	= sho\y#
							z#	= sho\z#
							sx#	= Rnd(2,2.5)
							sy# = Rnd(2,2.5)
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(230,255),Rnd(99,160),Rnd(60),Rand(50,99))
							sx# = Rnd(1.5,2)
							sy# = Rnd(1.5,2)
							FX_CreateExplosion(x#,y#,z#,sx#,sy#,Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(50,99))
							If sho\swc\shockwave
								Wea_CreateWave(x,y,z,sho\swc\hdamage,sho\swc\shockwave^100)
								If sho\swc\shockwave > 1.035
									fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
									fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),sho\swc\shockwave,1)
								EndIf
							EndIf
						EndIf
					EndIf
					EntityType sho\mesh,0
					FreeEntity sho\mesh
					Trail_Remove(sho\mesh)
					Delete sho.shoot
				EndIf
			EndIf

			Exit
		EndIf
	Next
End Function

Function Wea_SendTarget(sho.shoot)
	If net_isserver=1 And sho\tar <> Null
		AddUDPByte(C_ShotTarget)
		AddUDPShort(sho\id)
		AddUDPByte(sho\tar\id)
	EndIf
End Function

Function Wea_GetTarget()
	wid = ReadUDPShort()
	sid = ReadUDPByte()
	For sho.shoot = Each shoot
		If sho\id = wid
			s.ship	= ships.ship(sid)
			sho\tar = s
		EndIf
	Next
End Function

Function Wea_CreateWave(x#,y#,z#,power#,size#)
	sw.shockwave= New shockwave
	sw\x		= x
	sw\y		= y
	sw\z		= z
	sw\power	= power
	sw\size		= size
	If size > 70 Then FX_Fake3dSound(FX_ShockSFX,x,y,z,size/100.0)
End Function

Function Wea_UpdateWaves()
	For sw.shockwave = Each shockwave
		sw\i = sw\i + main_gspe*2
		
		If net_isserver = 1 Or net = 0
			For s.ship = Each ship
				dist# = Util_CoordinateDistance(s\x,s\y,s\z,sw\x,sw\y,sw\z)/2
				If dist < sw\i And s\spawntimer <= 0
					If dist < 2 Then dist = 2
					If s\shc\fixed = 0
						s\dx = s\dx+((s\x-sw\x)/(dist)) * sw\power / 350000 / s\shc\size^2 * main_gspe
						s\dy = s\dy+((s\y-sw\y)/(dist)) * sw\power / 350000 / s\shc\size^2 * main_gspe
						s\dz = s\dz+((s\z-sw\z)/(dist)) * sw\power / 350000 / s\shc\size^2 * main_gspe
					EndIf
					
					If s = main_pl Then cc_quake = cc_quake + .25
					
					s\shields = s\shields - sw\power / dist / sw\i * main_gspe
					If s\shields < 1 Then s\shields = 1 ElseIf s\shields > 100 And Rand(20)=10 Then FX_ShieldFX(s, sw\x#,sw\y#,sw\z#, 5)
					s\hitpoints = s\hitpoints - sw\power / 5 / dist / sw\i / s\shields * main_gspe
				EndIf
			Next
		Else
			s = main_pl
			If s <> Null And s\spawntimer <= 0
				dist# = Util_CoordinateDistance(s\x,s\y,s\z,sw\x,sw\y,sw\z)/2
				If dist < sw\i
					If dist < 2 Then dist = 2
					If s\shc\fixed = 0
						s\dx = s\dx+((s\x-sw\x)/(dist)) * sw\power / 350000 / s\shc\size^2 * main_gspe
						s\dy = s\dy+((s\y-sw\y)/(dist)) * sw\power / 350000 / s\shc\size^2 * main_gspe
						s\dz = s\dz+((s\z-sw\z)/(dist)) * sw\power / 350000 / s\shc\size^2 * main_gspe
					EndIf
					
					If s = main_pl Then cc_quake = cc_quake + .25
					
					If s\shields > 100 And Rand(20)=10 Then FX_ShieldFX(s, sw\x#,sw\y#,sw\z#, 5)
				EndIf
			EndIf
		EndIf
		
		If sw\i/2 > sw\size Then Delete sw
	Next
End Function

Function Wea_ClearShoots()
	For sho.shoot = Each shoot
		Trail_Remove(sho\mesh)
		FreeEntity sho\mesh
		Delete sho.shoot
	Next
End Function	

Function Wea_Clear()
	For w.weapon = Each weapon
		FreeEntity w\mesh
		If w\muzzle Then FreeEntity w\muzzle
		FreeSound w\sound
		FreeSound w\hitsound
		Delete w.weapon
	Next
	
	Wea_ClearShoots()
	
	Delete Each shockwave
	
	wea_sigcount = 0
	Dim weapsigf#(2,2,2)
	Dim weapsigi(2,2,2)
End Function