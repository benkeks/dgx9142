
Type asteroidcluster
	Field id
	Field x#,y#,z#
	Field range, count
	Field speed#, rotation#
	Field mesh, size#
End Type


Function Asteroids_CreateCluster(id, x#,y#,z#, range, count, speed#, rotation#, mesh, size#)
	a.asteroidcluster = New asteroidcluster
	a\id			= id
	a\x				= x
	a\y				= y
	a\z				= z
	a\range			= range
	a\count			= count
	a\speed			= speed
	a\rotation		= rotation
	a\mesh			= mesh
	a\size#			= size
	HideEntity mesh
	
	If net=0 Or net_isserver=1 Then Asteroids_CreateAsteroids(a)
End Function

Function Asteroids_CreateAsteroids(a.asteroidcluster)
	For i = 1 To a\count
		w.wreck = New wreck
		
		w\id = wreck_count
		wreck_count = wreck_count + 1
		
		
		w\dx	= Rnd(-a\speed, a\speed)
		w\dy	= Rnd(-a\speed, a\speed)
		w\dz	= Rnd(-a\speed, a\speed)
		
		w\tp	= Rnd(-a\rotation, a\rotation)
		w\ty	= Rnd(-a\rotation, a\rotation)
		w\tr	= Rnd(-a\rotation, a\rotation)
		
		w\size#	= a\size
		w\hitpoints = w\size*1000+100
		w\status = WRECK_DEFAULT
		
		w\mesh	= CopyEntity(a\mesh)
		
		w\typ	= WRECK_ASTEROID
		w\class	= a\id
		
		i2 = 0
		Repeat
			PositionEntity w\mesh, a\x, a\y, a\z
			TurnEntity w\mesh, Rnd(-180,180), Rnd(-180,180), 0
			MoveEntity w\mesh,0,0,Rnd(a\range)
			TurnEntity w\mesh, Rnd(-180,180), Rnd(-180,180), Rnd(-180,180)
			goback = 0
			For w2.wreck = Each wreck
				If w2 <> w
					If EntityDistance(w2\mesh, w\mesh) < (MeshDepth(w\mesh)+MeshDepth(w2\mesh)) Then goback = 1 Exit
				EndIf
			Next
			i2 = i2 + 1
		Until goback = 0 Or i2 > i+3
		
		EntityType w\mesh, Map_Colli
		EntityRadius w\mesh,MeshDepth(w\mesh)*.45
		EntityPickMode w\mesh,2
		ResetEntity w\mesh
		
		ShowEntity w\mesh
	Next
End Function

Function Asteroids_Reset()
	For a.asteroidcluster = Each asteroidcluster
		Asteroids_CreateAsteroids(a)
	Next
End Function

Function Asteroids_Clear()
	For a.asteroidcluster = Each asteroidcluster
		FreeEntity a\mesh
		Delete a
	Next
End Function

Function Asteroids_ParseCluster(stream)
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "id"
					id = paras[1]
				Case "x"
					x# = paras[1]
				Case "y"
					y# = paras[1]
				Case "z"
					z# = paras[1]
				Case "range"
					range = paras[1]
				Case "count"
					count = paras[1]
				Case "speed"
					speed# = paras[1]
				Case "rotation"
					rotation# = paras[1]
				Case "mesh"
					mesh = LoadMeshA(paras[1])
					If main_texdetail=2 Then
						EntityFX mesh,1
						If AEM_ReturnDot3 Then
							EntityTexture mesh,env_normaldif,0,0
							EntityTexture mesh,shi_specular2additive,0,2
						Else
							EntityTexture mesh,shi_specular2,0,2
						EndIf
					EndIf
				Case "size"
					size# = paras[1]
			End Select
		EndIf
	Until lin="}"
	
	Asteroids_CreateCluster(id, x#,y#,z#, range, count, speed#, rotation#, mesh, size#)
End Function