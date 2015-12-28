Type explosion
	Field typ
	Field sur
	Field x#,y#,z#
	Field rota#
	Field sx#,sy#,tx#,ty#
	Field scalefact#
	Field fadeout
	Field r,g,b,a#
	Field time#,endtime
End Type

Global main_gspe#,util_lastmsc,main_mscleft# ; für den Timer...


Global FX_explosion,fx_explosur,fx_explotex
Global FX_explosion2,fx_explo2sur,fx_explo2tex

Graphics3D 800,600,32,2
SetBuffer BackBuffer()

Global cam_cam = CreateCamera()

FX_Init
Util_InitTimer()

Repeat
	Util_Timer
	
	Cls
	FX_Update()
	
	;If Rand(10)=5 Then Shi_Explode(0,0,100)
	
	Shi_Explode(Rand(-100,100),Rand(-80,80),Rand(10,100),Rnd(.7,3))
	
	RenderWorld
	
	Text 0,0,Int(1000/main_mscleft)
	Text 0,15,TrisRendered()
	
	Flip 0
Until KeyHit(1)

FX_Clear

End


Function Util_InitTimer()
	util_lastmsc = MilliSecs()
End Function

Function Util_Timer()
	msc = MilliSecs()
	main_mscleft = Float#(msc-util_lastmsc)
	main_gspe# = main_mscleft/13.00
	util_lastmsc = msc
End Function


Function Shi_Explode(x#,y#,z#,s#=1,t#=1)
	For i = 0 To 2*main_particledetail
	fx_createexplosion(x+Rnd(-3,3),y+Rnd(-3,3),z+Rnd(-3,3),s*Rnd(7,9),s*Rnd(7,9),Rnd(230,255),Rnd(100,160),Rnd(60),Rand(70,150)*t)
	Next
	For i = 0 To 1*main_particledetail
	fx_createexplosion(x+Rnd(-1,1),y+Rnd(-1,1),z+Rnd(-1,1),s*Rnd(4,5),s*Rnd(4,5),Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(60,110)*t)
	Next
	For i = 0 To 1*main_particledetail
		fx_createexplosion2(x+Rnd(-3,3),y+Rnd(-3,3),z+Rnd(-3,3),s*Rnd(2,3),s*Rnd(2,3),Rnd(230,255),Rnd(110,200),Rnd(90),Rand(100,200),Rnd(1.02,1.025),1)
	Next
End Function

Function FX_Init()
	FX_explotex = LoadTexture("explosion.bmp",2)
	fx_explosion= CreateMesh(cam_cam)
	fx_explosur	= CreateSurface(fx_explosion)
	EntityBlend fx_explosion,3
	EntityFX fx_explosion,1+2+16+32
	EntityTexture fx_explosion,fx_explotex
	
	FX_explo2tex	= LoadTexture("explopar.bmp",2)
	fx_explosion2	= CreateMesh(cam_cam)
	fx_explo2sur	= CreateSurface(fx_explosion2)
	EntityBlend fx_explosion2,3
	EntityFX fx_explosion2,1+2+16+32
	EntityTexture fx_explosion2,fx_explo2tex
End Function

Function FX_Update()
	FX_UpdateExplosions()
End Function

Function FX_Clear()
	For e.explosion = Each explosion
		Delete e.explosion
	Next
	FreeEntity fx_explosion
	FreeTexture fx_explotex
	FreeEntity fx_explosion2
	FreeTexture fx_explo2tex
End Function

Function FX_CreateExplosion(x#,y#,z#,sx#=5,sy#=5,r=255,g=255,b=255,endtime=100,scalefact#=1,fadeout=0)
	e.explosion = New explosion
	e\sur		= fx_explosur
	e\typ		= 1
	
	e\x			= x
	e\y			= y
	e\z			= z
	
	rota		= Rand(360)
	e\rota		= rota
	e\sx		= -sx*Cos(rota) - sy*Sin(rota)
	e\sy		= sy*Cos(rota) + -sx*Sin(rota)
	
	e\tx		= sx*Cos(rota) - sy*Sin(rota)
	e\ty		= sy*Cos(rota) + sx*Sin(rota)
	
	e\r			= r
	e\g			= g
	e\b			= b
	e\a			= 255
	
	e\time#		= 0
	e\endtime	= endtime
	e\scalefact	= scalefact
	e\fadeout	= fadeout
End Function

Function FX_CreateExplosion2(x#,y#,z#,sx#=5,sy#=5,r=255,g=255,b=255,endtime=100,scalefact#=1,fadeout=0)
	e.explosion = New explosion
	e\sur		= fx_explo2sur
	e\typ		= 2
	
	e\x			= x
	e\y			= y
	e\z			= z
	
	rota		= Rand(360)
	e\rota		= rota
	e\sx		= -sx*Cos(rota) - sy*Sin(rota)
	e\sy		= sy*Cos(rota) + -sx*Sin(rota)
	
	e\tx		= sx*Cos(rota) - sy*Sin(rota)
	e\ty		= sy*Cos(rota) + sx*Sin(rota)
	
	e\r			= r
	e\g			= g
	e\b			= b
	e\a			= 1
	
	e\time#		= 0
	e\endtime	= endtime
	e\scalefact	= scalefact
	e\fadeout	= fadeout
	
	;FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,.5+sx/120)
	;chan = EmitSound(fx_explosound[Rand(0,3)],e\mesh)
	;ChannelPitch chan,Rand(8000,22000)
	;ChannelVolume chan,sx/7
	;Return e\mesh
End Function

Function FX_UpdateExplosions()
	Local vert[3]
	
	ClearSurface fx_explosur
	ClearSurface fx_explo2sur

	Local c# = Float(1)/Float(40)
	
	For e.explosion = Each explosion
		e\time	= e\time + main_gspe
		
		TFormPoint e\x,e\y,e\z,0,fx_explosion
		x#	= TFormedX()
		y#	= TFormedY()
		z#	= TFormedZ()
		
		If e\scalefact
			fact# = e\scalefact^main_gspe
			e\sx = e\sx * fact#
			e\sy = e\sy * fact#
			e\tx = e\tx * fact#
			e\ty = e\ty * fact#
		EndIf
		
		If e\fadeout = 1
			e\a = 1-Float(e\time)/Float(e\endtime)
		EndIf
		
		If z > 0
			Select e\typ
			Case 1
				p# = Float(Int(39*Float(e\time#/Float(e\endtime))))/Float(40)
				vert[0] = AddVertex(e\sur,x+e\sx,y+e\sy,z,p,0) ; ol
				vert[1] = AddVertex(e\sur,x+e\tx,y+e\ty,z,p+c,0) ; or
				vert[2] = AddVertex(e\sur,x-e\tx,y-e\ty,z,p,1) ; ul
				vert[3] = AddVertex(e\sur,x-e\sx,y-e\sy,z,p+c,1) ; ur
			Case 2
				e\scalefact = e\scalefact * .9999 ^ main_gspe
				vert[0] = AddVertex(e\sur,x+e\sx,y+e\sy,z,0,0) ; ol
				vert[1] = AddVertex(e\sur,x+e\tx,y+e\ty,z,1,0) ; or
				vert[2] = AddVertex(e\sur,x-e\tx,y-e\ty,z,0,1) ; ul
				vert[3] = AddVertex(e\sur,x-e\sx,y-e\sy,z,1,1) ; ur
			End Select
			
			VertexColor e\sur,vert[0],e\r,e\g,e\b,e\a
			VertexColor e\sur,vert[1],e\r,e\g,e\b,e\a
			VertexColor e\sur,vert[2],e\r,e\g,e\b,e\a
			VertexColor e\sur,vert[3],e\r,e\g,e\b,e\a
			
			AddTriangle e\sur,vert[0],vert[1],vert[2]
			AddTriangle e\sur,vert[2],vert[1],vert[3]
		EndIf
		
		
		If e\time >= e\endtime Then Delete e.explosion
	Next
End Function