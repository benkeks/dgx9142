
Global dust_camerajump

Const DUST_DUST = 0
Const DUST_VEG	= 1

Type dust
	Field typ
	Field mesh, sur, tex
	Field range#
	Field number
	Field minsize#,maxsize#
	Field ratio#
	Field terrain
	Field map
	;Field nextmsc
End Type

Type dustparticle
	Field x#,y#,z#
	Field size#
	Field turn#, turnspeed#
	Field dc.dust
End Type


Function Dust_ParseDust(stream)
	d.dust 	= New dust
	d\ratio	= 1
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "range"
				d\range	= paras[1]
			Case "number"
				d\number= Int(paras[1])*main_detail^2
			Case "size","minsize"
				d\minsize= paras[1]
			Case "maxsize"
				d\maxsize= paras[1]
			Case "ratio"
				d\ratio	= paras[1]
			Case "texture"
				tex$	= paras[1]
			End Select
		EndIf
	Until lin="}"
	d\tex	= LoadTexture(tex,1+2)
	d\mesh	= CreateMesh()
	d\sur	= CreateSurface(d\mesh)
	EntityTexture d\mesh,d\tex
	EntityFX d\mesh,1+2+8+16+32
	EntityBlend d\mesh,3
End Function

Function Dust_ParseVegetation(stream)
	d.dust	= New dust
	d\typ	= DUST_VEG
	d\ratio	= 1
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
			Case "range"
				d\range	= paras[1]
			Case "number"
				d\number= Int(paras[1])*main_particledetail
			Case "size","minsize"
				d\minsize= paras[1]
			Case "maxsize"
				d\maxsize= paras[1]
			Case "ratio"
				d\ratio	= paras[1]
			Case "texture"
				tex$	= paras[1]
			Case "terrain"
				o.obj	= Obj_FindObject(paras[1])
				d\terrain = o\entity
			Case "map"
				d\map	= LoadImage(paras[1])
			End Select
		EndIf
	Until lin="}"
	d\tex	= LoadTexture(tex,1+4+8)
	d\mesh	= CreateMesh()
	d\sur	= CreateSurface(d\mesh)
	EntityTexture d\mesh,d\tex
	EntityFX d\mesh,1+16
	EntityBlend d\mesh,1
End Function

Function Dust_Update()
	Local vert[3]
	cam = cc_cam
	
	If dust_camerajump = 2 Then Delete Each dustparticle
	
	For d.dust = Each dust
		;Stop
		;If util_lastmsc > d\nextmsc Then 
			PositionEntity d\mesh,EntityX(cam,1),EntityY(cam,1),EntityZ(cam,1)
			RotateEntity d\mesh, EntityPitch(cam,1)*(d\typ<>DUST_VEG),EntityYaw(cam,1),EntityRoll(cam,1)*(d\typ<>DUST_VEG)
			If (d\typ=DUST_VEG) Then TurnEntity d\mesh, 0,0,Sin(MilliSecs()/10)*3
			ClearSurface d\sur
			count = 0
			For p.dustparticle = Each dustparticle
				If d = p\dc
					count = count + 1
					
					TFormPoint p\x,p\y,p\z,0,d\mesh
					x#	= TFormedX()
					y#	= TFormedY()
					z#	= TFormedZ()
					
					If z > 0
						Select d\typ
						Case DUST_DUST
							alpha# = .9 - Abs(z-d\range/2) / d\range * 2
							
							a1# = Sin(p\turn)
							a2# = Cos(p\turn)
							p\turn = p\turn + p\turnspeed*main_gspe
							If main_pl\spawntimer <= 0 Then p\turn = p\turn - main_pl\roll * main_gspe
							
							vert[0] = AddVertex(d\sur,x-p\size*a1,y+p\size*d\ratio*a2,z,0,0) ; ol
							vert[1] = AddVertex(d\sur,x+p\size*a2,y+p\size*d\ratio*a1,z,1,0) ; or
							vert[2] = AddVertex(d\sur,x-p\size*a2,y-p\size*d\ratio*a1,z,0,1) ; ul
							vert[3] = AddVertex(d\sur,x+p\size*a1,y-p\size*d\ratio*a2,z,1,1) ; ur
							
							VertexColor d\sur,vert[0],255,255,255, alpha
							VertexColor d\sur,vert[1],255,255,255, alpha
							VertexColor d\sur,vert[2],255,255,255, alpha
							VertexColor d\sur,vert[3],255,255,255, alpha
						Case DUST_VEG
							If z>d\range*.75 Then
								y = y + p\size*d\ratio * 8 * (1.0-z/d\range)
							Else
								y = y + p\size*d\ratio * 2
							EndIf
							vert[0] = AddVertex(d\sur,x-p\size,y+p\size*d\ratio,z,0,0) ; ol
							vert[1] = AddVertex(d\sur,x+p\size,y+p\size*d\ratio,z,1,0) ; or
							vert[2] = AddVertex(d\sur,x-p\size,y-p\size*d\ratio,z,0,1) ; ul
							vert[3] = AddVertex(d\sur,x+p\size,y-p\size*d\ratio,z,1,1) ; ur
						End Select
						
						AddTriangle d\sur,vert[0],vert[1],vert[2]
						AddTriangle d\sur,vert[2],vert[1],vert[3]
					EndIf
					
					If Abs(x) > d\range*1.11 Or (Abs(y) > d\range*1.11 And d\typ<>DUST_VEG) Or Abs(z) > d\range*1.11 Or dust_camerajump
						Delete p
					EndIf
					
				EndIf
			Next
			If count < d\number
				If d\map <> 0 Then LockBuffer ImageBuffer(d\map)
				For i = count To d\number
					p.dustparticle = New dustparticle
					Repeat
						p\x = EntityX(d\mesh,1)+Rnd(-d\range,d\range)*1
						p\y = EntityY(d\mesh,1)+Rnd(-d\range,d\range)*1
						p\z = EntityZ(d\mesh,1)+Rnd(-d\range,d\range)*1
						
						If dust_camerajump = 0
							Select Rand(1,6-(d\typ=DUST_VEG)*2)
							Case 1
								p\x = EntityX(d\mesh,1)-d\range*Rnd(.9,1.1)
							Case 2
								p\x = EntityX(d\mesh,1)+d\range*Rnd(.9,1.1)
							Case 3
								p\z = EntityZ(d\mesh,1)-d\range*Rnd(.9,1.1)
							Case 4
								p\z = EntityZ(d\mesh,1)+d\range*Rnd(.9,1.1)
							Case 5
								p\y = EntityY(d\mesh,1)-d\range*Rnd(.9,1.1)
							Case 6
								p\y = EntityY(d\mesh,1)+d\range*Rnd(.9,1.1)
							End Select
						EndIf
						fin = 1
						If d\map <> 0
							TFormPoint p\x,p\y,p\z, 0,d\terrain
							If ReadPixelFast(TFormedX()*128/TerrainSize(d\terrain), 128-TFormedZ()*128/TerrainSize(d\terrain),ImageBuffer(d\map)) <> $FFFFFFFF Then
								fin = 0
							EndIf
						EndIf
					Until fin
					p\turn = Rand(360)
					p\turnspeed = Rnd(-.1,.1)
					p\size = Rnd(d\minsize,d\maxsize)
					If d\typ = DUST_VEG Then p\y = TerrainY(d\terrain, p\x,p\y,p\z)-p\size*d\ratio*1.15
					
					p\dc = d
				Next
				If d\map <> 0 Then UnlockBuffer ImageBuffer(d\map)
			EndIf
			;d\nextmsc = util_lastmsc+50
		;EndIf
	Next
	If dust_camerajump = 1 Then
		dust_camerajump = 2
	Else
		dust_camerajump = 0
	EndIf
End Function

Function Dust_Clear()
	For d.dust = Each dust
		FreeEntity d\mesh
		FreeTexture d\tex
		FreeImage d\map
		Delete d
	Next
	
	Delete Each dustparticle
End Function