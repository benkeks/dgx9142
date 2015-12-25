Type wreck
	Field id, class
	Field typ	; 0 = ship	1 = asteroid
	Field size#
	Field mesh
	Field dx#,dy#,dz#
	Field tp#, ty#, tr#
	Field hitpoints#
	Field status ; 0 = default 1 = burning
End Type

Global wreck_tex 

Const WRECK_DEFAULT = 0
Const WRECK_BURNING = 1

Const WRECK_SHIP	= 0
Const WRECK_ASTEROID= 1

Global wreck_count = 0

Function Wreck_Init()
	wreck_tex = LoadTexture("GFX/SPRITES/damage.png",1)
	TextureBlend wreck_tex,2
	
	wreck_count = 0
End Function

Function Wreck_Create.wreck(s.ship)
	w.wreck = New wreck
	
	w\id = wreck_count
	wreck_count = wreck_count + 1
	
	w\dx	= s\dx+Rnd(-.1,.1)
	w\dy	= s\dy+Rnd(-.1,.1)
	w\dz	= s\dz+Rnd(-.1,.1)
	
	w\tp	= s\tspitch+Rnd(-.3,.1)
	w\ty	= s\tsyaw+Rnd(-.1,.1)
	w\tr	= s\roll*(net=0)+Rnd(-.1,.1)
	
	w\size#	= s\shc\size
	w\hitpoints = w\size*100+100
	
	If s\hmesh Then EntityParent s\hmesh,0
	EntityParent s\shield, 0
	w\mesh	= CopyEntity(s\mesh,s\mesh)
	EntityType w\mesh, Shi_ColliBig2
	EntityRadius w\mesh,MeshDepth(w\mesh)*.45

	EntityTexture w\mesh, wreck_tex,0,4
	EntityParent w\mesh,0
	EntityParent s\shield,s\mesh
	If s\hmesh Then 
		EntityParent s\hmesh,s\mesh
	EndIf
	c = CountChildren(w\mesh)
	For i = 1 To c
		FreeEntity GetChild(w\mesh,1)
	Next
	
	w\status = WRECK_BURNING
	w\typ	 = WRECK_SHIP
	w\class	 = s\id
	
	Return w
End Function

Function Wreck_SendWreck(w.Wreck) ; 4 + 4 * 8 = 36 bytes
	If net_isserver
		AddUDPByte(C_WreckData)
		AddUDPShort(w\id)
		
		AddUDPByte(w\typ)
		AddUDPByte(w\class)
		
		AddUDPFloat(w\size)
		
		AddUDPFloat(w\dx)
		AddUDPFloat(w\dy)
		AddUDPFloat(w\dz)
		
		AddUDPFloat(w\tp)
		AddUDPFloat(w\ty)
		AddUDPFloat(w\tr)
		
		AddUDPFloat(EntityX(w\mesh,1))
		AddUDPFloat(EntityY(w\mesh,1))
		AddUDPFloat(EntityZ(w\mesh,1))
		
		AddUDPFloat(EntityPitch(w\mesh,1))
		AddUDPFloat(EntityYaw(w\mesh,1))
		AddUDPFloat(EntityRoll(w\mesh,1))
		
		AddUDPFloat(w\hitpoints)
		AddUDPByte(w\status)
	EndIf
End Function

Function Wreck_GetWreck()
	If net = 1
		id		= ReadUDPShort()
		typ		= ReadUDPByte()
		class	= ReadUDPByte()
		
		size#	= ReadUDPFloat()
		
		dx#		= ReadUDPFloat()
		dy#		= ReadUDPFloat()
		dz#		= ReadUDPFloat()
		
		tp#		= ReadUDPFloat()
		ty#		= ReadUDPFloat()
		tr#		= ReadUDPFloat()
		
		x#		= ReadUDPFloat()
		y#		= ReadUDPFloat()
		z#		= ReadUDPFloat()
		
		p#		= ReadUDPFloat()
		ya#		= ReadUDPFloat()
		r#		= ReadUDPFloat()
		
		hitpoints#	= ReadUDPFloat()
		status		= ReadUDPByte()
		
		If typ = WRECK_ASTEROID
			For a.asteroidcluster = Each asteroidcluster
				If a\id = class Then Exit
			Next
			w.wreck = New wreck
			w\id	= id
			w\class	= class
			w\size	= size
			w\dx	= dx
			w\dy	= dy
			w\dz	= dz
			w\tp	= tp
			w\ty	= ty
			w\tr	= tr
			w\hitpoints	= hitpoints
			w\status	= status
			
			w\mesh	= CopyEntity(a\mesh)
			
			PositionEntity w\mesh, x, y, z
			RotateEntity w\mesh, p,ya,r
			
			EntityType w\mesh, Map_Colli
			EntityRadius w\mesh,MeshDepth(w\mesh)*.45
			EntityPickMode w\mesh,2
			ResetEntity w\mesh
			
			ShowEntity w\mesh
		EndIf
	EndIf
End Function

Function Wreck_SendWreckDestruction(w.wreck)
	If net_isserver
		AddUDPByte(C_WreckDestruction)
		AddUDPShort(w\id)
	EndIf
End Function

Function Wreck_GetWreckDestruction()
	If net=1 And net_isserver=0
		id = ReadUDPShort()
		For w.wreck = Each wreck
			If w\id = id Then w\hitpoints = -1
		Next
	EndIf
End Function

Function Wreck_Update()
	For w.wreck = Each wreck
		w\dy = w\dy - map_gravi * .5 *main_gspe
		TurnEntity w\mesh, w\tp*main_gspe,w\ty*main_gspe,w\tr*main_gspe
		TranslateEntity w\mesh, w\dx*main_gspe,w\dy*main_gspe,w\dz*main_gspe
		If CountCollisions(w\mesh) Then w\hitpoints = w\hitpoints - Rand(40,100)*main_gspe
		If w\status = WRECK_BURNING Then w\hitpoints = w\hitpoints - Rand(0,10)*main_gspe
		If w\hitpoints <= 0 Then
			For i = 0 To w\size/2
				x# = Rnd(-MeshWidth(w\mesh)/3,MeshWidth(w\mesh)/3)
				y# = Rnd(-MeshHeight(w\mesh)/3,MeshHeight(w\mesh)/3)
				z# = Rnd(-MeshDepth(w\mesh)/3,MeshDepth(w\mesh)/3)
				TFormPoint x,y,z,w\mesh,0
				x = TFormedX()
				y = TFormedY()
				z = TFormedZ()
				shi_explode(x#,y#,z#,w\size/2+.5+Rnd(-1.0,1.2),w\size/5+.8)
			Next
			For i = 1 To w\size / 4
				x# = Rnd(-MeshWidth(w\mesh)/3,MeshWidth(w\mesh)/3)
				y# = Rnd(-MeshHeight(w\mesh)/3,MeshHeight(w\mesh)/3)
				z# = Rnd(-MeshDepth(w\mesh)/3,MeshDepth(w\mesh)/3)
				TFormPoint x,y,z,w\mesh,0
				x = TFormedX()
				y = TFormedY()
				z = TFormedZ()
				
				Wea_CreateWave(x,y,z,Rand(1000,2000),2)
				fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),Rnd(1.03,1.05),1)
				fx_createexplosion3(x,y,z,2,2,355,355,355,Rand(100,150),Rnd(1.03,1.05),1)
			Next
			If net_isserver = 1 Then Wreck_SendWreckDestruction(w)
			Wreck_Delete(w)
		ElseIf Rand(0,8)=1 And w\status = WRECK_BURNING
			x# = Rnd(-MeshWidth(w\mesh)/3,MeshWidth(w\mesh)/3)
			y# = Rnd(-MeshHeight(w\mesh)/3,MeshHeight(w\mesh)/3)
			z# = Rnd(-MeshDepth(w\mesh)/3,MeshDepth(w\mesh)/3)
			TFormPoint x,y,z,w\mesh,0
			x = TFormedX()
			y = TFormedY()
			z = TFormedZ()
			s# = w\size/2+.5+Rnd(-1.0,1.2)
			t# = w\size/5+.8
			fx_createexplosion(x+Rnd(-1,1),y+Rnd(-1,1),z+Rnd(-1,1),s*Rnd(4,5),s*Rnd(4,5),Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(60,110)*t)	
			fx_createexplosion2(x+Rnd(-3,3),y+Rnd(-3,3),z+Rnd(-3,3),s*Rnd(2,3),s*Rnd(2,3),Rnd(230,255),Rnd(110,200),Rnd(90),Rand(100,200),Rnd(1.02,1.025),1)
			FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,1+s/160)
		EndIf
	Next
End Function

Function Wreck_Delete(w.wreck)
	FreeEntity w\mesh
	Delete w
End Function

Function Wreck_Clear(all=0)
	For w.wreck = Each wreck
		Wreck_Delete(w)
	Next
	If all Then FreeTexture wreck_tex
End Function