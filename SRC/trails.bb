Const TRAIL_MAXLEN = 12

Type trail
	Field ent
	Field par
	Field x#[TRAIL_MAXLEN]
	Field y#[TRAIL_MAXLEN]
	Field z#[TRAIL_MAXLEN]
	Field Pit#[TRAIL_MAXLEN],ya#[TRAIL_MAXLEN],ro#[TRAIL_MAXLEN]
	Field timer, interval
	Field tlen,reallen
	Field r,g,b
	Field size#
	Field speed#
End Type

Global trail_mesh, trail_sur, trail_tex, trail_timer
Global trail_count

Function Trail_Init()
	trail_mesh	= CreateMesh()
	trail_sur	= CreateSurface(trail_mesh)
	trail_tex	= LoadTexture(gfxd+"WEAPONS/trail.png",1+2+16+32)
	
	EntityTexture trail_mesh,trail_tex
	EntityFX trail_mesh,1+2+16+32
	EntityBlend trail_mesh,3
End Function


Function Trail_Assign(ent,par,tLen,r,g,b,size#=1,speed#=0, interval=70)
	t.trail = New trail
	t\ent	= ent
	t\par	= par
	tlen	= tlen * main_particledetail
	If tlen > TRAIL_MAXLEN Then tlen = TRAIL_MAXLEN
	t\tlen	= tlen
	t\r 	= r
	t\g		= g
	t\b		= b
	t\size	= size
	t\speed = speed
	t\interval = interval
End Function

Function Trail_Update()
	Local x#[3],y#[3],z#[3]
	Local x2#[3],y2#[3],z2#[3]
	Local vert[8]
	
	Local lenoff#
	
	If trail_timer < MilliSecs()
		trail_timer = MilliSecs() + 5
		
		PositionEntity trail_mesh,EntityX(cc_cam,1),EntityY(cc_cam,1),EntityZ(cc_cam,1)
		RotateEntity trail_mesh,EntityPitch(cc_cam,1),EntityYaw(cc_cam,1),EntityRoll(cc_cam,1)
		
		sur = trail_sur
		ClearSurface sur
		
		piv = CreatePivot()
		
		For t.trail = Each trail
			count = count + 1
			If main_dedicate = 0
				If MilliSecs() > t\timer And t\par<>0
					For i = t\reallen+1 To 0 Step -1
						If i > 0 And i <= t\tlen
							t\x[i] = t\x[i-1]
							t\y[i] = t\y[i-1]
							t\z[i] = t\z[i-1]
							
							t\pit[i] = t\pit[i-1]
							t\ya[i] = t\ya[i-1]
							t\ro[i] = t\ro[i-1]
						ElseIf i = 0
							t\x[i] = EntityX(t\ent,1)
							t\y[i] = EntityY(t\ent,1)
							t\z[i] = EntityZ(t\ent,1)
							
							t\pit[i] = EntityPitch(t\ent,1)
							t\ya[i] = EntityYaw(t\ent,1)
							t\ro[i] = EntityRoll(t\ent,1)
						EndIf
					Next
					If t\reallen < t\tlen Then t\reallen = t\reallen + 1
					t\timer = MilliSecs()+t\interval
				ElseIf MilliSecs() > t\timer 
					For i = t\reallen+1 To 0 Step -1
						If i > 0 And i <= t\tlen
							t\x[i] = t\x[i-1]
							t\y[i] = t\y[i-1]
							t\z[i] = t\z[i-1]
							
							t\pit[i] = t\pit[i-1]
							t\ya[i] = t\ya[i-1]
							t\ro[i] = t\ro[i-1]
						EndIf
					Next
					If t\reallen > 0 Then t\reallen = t\reallen - 1
					t\timer = MilliSecs()+t\interval
				EndIf
				
				If t\par <> 0
					TFormPoint t\size,0,0, t\ent,trail_mesh
					x2[0] = TFormedX()
					y2[0] = TFormedY()
					z2[0] = TFormedZ()
					
					TFormPoint -t\size,0,0, t\ent,trail_mesh
					x2[1] = TFormedX()
					y2[1] = TFormedY()
					z2[1] = TFormedZ()
					
					TFormPoint 0,t\size,0, t\ent,trail_mesh
					x2[2] = TFormedX()
					y2[2] = TFormedY()
					z2[2] = TFormedZ()
					
					TFormPoint 0,-t\size,0, t\ent,trail_mesh
					x2[3] = TFormedX()
					y2[3] = TFormedY()
					z2[3] = TFormedZ()
				EndIf
				
				off# = (t\timer-MilliSecs())/Float(t\interval)
				lenoff# = (1.0) / (t\tlen)
				
				a# = 0
				
				a# = 1.0-Float(t\reallen) / t\tlen - lenoff/2 + (lenoff - off * lenoff)*(t\reallen >= t\tlen)
				If t\par = 0 Then a# = a# + lenoff
				
				For i = 0 To t\reallen-1
					PositionEntity piv,t\x[i],t\y[i],t\z[i]
					RotateEntity piv,t\pit[i],t\ya[i],t\ro[i]
					
					;MoveEntity piv,0,0,-t\speed*main_gspe
					t\x[i] = EntityX(piv)
					t\y[i] = EntityY(piv)
					t\z[i] = EntityZ(piv)
					
					TFormPoint t\size,0,0, piv,trail_mesh
					x[0] = TFormedX()
					y[0] = TFormedY()
					z[0] = TFormedZ()
					
					TFormPoint -t\size,0,0, piv,trail_mesh
					x[1] = TFormedX()
					y[1] = TFormedY()
					z[1] = TFormedZ()
					
					TFormPoint 0,t\size,0, piv,trail_mesh
					x[2] = TFormedX()
					y[2] = TFormedY()
					z[2] = TFormedZ()
					
					TFormPoint 0,-t\size,0, piv,trail_mesh
					x[3] = TFormedX()
					y[3] = TFormedY()
					z[3] = TFormedZ()
					
					If (t\par <> 0 Or i > 0) And z[0] > -100
						vert[5] = AddVertex(sur,x2[0],y2[0],z2[0], 0,a#) 
						vert[6] = AddVertex(sur,x2[1],y2[1],z2[1], 1,a)
						vert[7] = AddVertex(sur,x2[2],y2[2],z2[2], 0,a)
						vert[8] = AddVertex(sur,x2[3],y2[3],z2[3], 1,a) 
						
						;If a = 0 Then
						;	a# = 1.0-Float(t\reallen) / t\tlen - lenoff/2 + (lenoff - off * lenoff)*(t\reallen >= t\tlen)
						;	If t\par = 0 Then a# = a# + lenoff
						;Else
							a# = a# + lenoff#
						;EndIf
						
						vert[1] = AddVertex(sur,x[0],y[0],z[0], 0,a) 
						vert[2] = AddVertex(sur,x[1],y[1],z[1], 1,a)
						vert[3] = AddVertex(sur,x[2],y[2],z[2], 0,a)
						vert[4] = AddVertex(sur,x[3],y[3],z[3], 1,a)
						
						;If i = t\reallen - 1 Then a# = 0 Else a# = 1.0-Float(i-off)/(t\tlen-1)
						;If z[0]<=6 Then 
						;	For i2 = 1 To 4
						;		VertexColor sur,vert[i2],t\r,t\g,t\b,(z[0]-2)/4.0
						;	Next
						;EndIf
						;
						;If z2[0]<=6 Then 
						;	For i2 = 5 To 8
						;		VertexColor sur,vert[i2],t\r,t\g,t\b,(z2[0]-2)/4.0
						;	Next
						;EndIf
						
						AddTriangle sur, vert[1],vert[2],vert[5]
						AddTriangle sur, vert[6],vert[2],vert[5]
						
						AddTriangle sur, vert[3],vert[4],vert[7]
						AddTriangle sur, vert[8],vert[4],vert[7]
					EndIf
					
					If main_detail < 1
						If z[0] > 300 Then i = i + 1
						If z[0] > 600 Then i = i + 1
					EndIf
					
					For i2 = 0 To 3
						x2[i2] = x[i2]
						y2[i2] = y[i2]
						z2[i2] = z[i2]
					Next
				Next
			EndIf
			
			If t\reallen<1 And t\par=0
				Delete t
			EndIf
		Next
		trail_count = count
		FreeEntity piv
	EndIf
End Function


Function Trail_Remove(par)
	For t.trail = Each trail
		If t\par = par Then t\par = 0
	Next
End Function

Function Trail_Clear(all=0)
	If all
		FreeEntity trail_mesh
		FreeTexture trail_tex
	EndIf
	For t.trail = Each trail
		If all=1 Or t\par = 0 Then Delete t Else t\reallen = 0
	Next
End Function