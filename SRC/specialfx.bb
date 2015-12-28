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

Type spark
	Field mesh
	Field sx#,sy#
	Field s.ship
End Type

Type shipfx
	Field typ
	Field mesh
	Field speed#
	Field anim#
	Field size#
	Field s.ship
End Type

Type warp
	Field mesh
	Field s.ship
	Field size#
	Field time
End Type


Global FX_explosion,fx_explosur,fx_explotex,fx_shocktex[9]
Global FX_Explosound[4]

Global FX_explosion2,fx_explo2sur,fx_explo2tex
Global FX_explosion3,fx_explo3sur
Global FX_explosion4,fx_explo4sur,fx_explo4tex

Global FX_Warp, FX_Shield

Global fx_ents[2]
Global FX_WarpSFX, FX_ShockSFX

Const FX_FadeOut = 1
Const FX_ScaleDown = 2

Function FX_Init()
	fx_ents[1]	= LoadSprite("gfx/sprites/engine.bmp",1+2)
	HideEntity fx_ents[1]
	fx_ents[2]	= LoadSprite("gfx/sprites/engine2.bmp",1+2)
	HideEntity fx_ents[2]
	
	For i = 0 To 9
		fx_shocktex[i] = LoadTexture("GFX/Sprites/shock/bild"+Int(i+1)+".png",1+2)
	Next 
	For i = 0 To 3
		fx_explosound[i] = Load3DSound("SFX/explo/explode"+Int(i+1)+".wav")
	Next
	
	FX_WarpSFX = Load3DSound("SFX/ENGINE/warp.ogg")
	FX_ShockSFX = Load3DSound("SFX/EXPLO/shock.mp3")
	
	FX_explotex = LoadTexture("GFX/Sprites/explosion.bmp",1+2)
	fx_explosion= CreateMesh(cc_cam)
	fx_explosur	= CreateSurface(fx_explosion)
	EntityBlend fx_explosion,3
	EntityFX fx_explosion,1+2+16+32
	EntityTexture fx_explosion,fx_explotex
	
	
	FX_explo2tex	= LoadTexture("GFX/Sprites/explopar.png",1+2)
	fx_explosion2	= CreateMesh(cc_cam)
	fx_explo2sur	= CreateSurface(fx_explosion2)
	EntityBlend fx_explosion2,3
	EntityFX fx_explosion2,1+2+16+32
	EntityTexture fx_explosion2,fx_explo2tex
	
	
	fx_explosion3	= CreateMesh(cc_cam)
	fx_explo3sur	= CreateSurface(fx_explosion3)
	EntityBlend fx_explosion3,3
	EntityFX fx_explosion3,1+2+16+32
	EntityTexture fx_explosion3,fx_shocktex[0]
	
	FX_explo4tex	= LoadTexture("GFX/Sprites/plasma.png",1+2)
	fx_explosion4	= CreateMesh(cc_cam)
	fx_explo4sur	= CreateSurface(fx_explosion4)
	EntityBlend fx_explosion4,3
	EntityFX fx_explosion4,1+2+16+32
	EntityTexture fx_explosion4,fx_explo4tex
	
	ClearTextureFilters 
	TextureFilter "",1+8
	FX_Warp		= LoadMesh("GFX/MISC/warp.b3d")
	HideEntity FX_Warp
	
	FX_Shield	= LoadMesh("GFX/MISC/shield.b3d")
	EntityFX fx_shield,1+16
	EntityBlend fx_shield,3
	ScaleMesh fx_Shield,.13,.13,.13
	HideEntity FX_Shield
End Function

Function FX_Update()
	FX_UpdateExplosions()
	FX_UpdateSparks()
	FX_UpdateShipFX()
	FX_UpdateWarps()
End Function

Function FX_UpdatePause()
	FX_UpdateExplosions(True)
End Function

Function FX_Clear()
	For e.explosion = Each explosion
		Delete e.explosion
	Next
	For i = 0 To 9
		FreeTexture fx_shocktex[i]
	Next 
	For i = 0 To 3
		FreeSound fx_explosound[i]
	Next
	FreeSound FX_WarpSFX
	For w.warp = Each warp
		FreeEntity w\mesh
		Delete w
	Next
	FreeEntity fx_explosion
	FreeTexture fx_explotex
	FreeEntity fx_explosion2
	FreeTexture fx_explo2tex
	FreeEntity fx_explosion4
	FreeTexture fx_explo4tex
	Delete Each spark
	
	FreeEntity fx_ents[1]
	FreeEntity fx_ents[2]
	
	FreeEntity FX_Warp
	FreeEntity FX_Shield
	
	FreeSound FX_ShockSFX
End Function

Function FX_ResetExplosions()
	Delete Each explosion
End Function

Function FX_UpdateSparks()
	For sp.spark = Each spark
		size# = Rnd(.3,.4)+Abs(sp\s\zs+sp\s\burnafter*sp\s\shc\afterburner)/sp\s\shc\topspeed
		ScaleSprite sp\mesh,sp\sx*size,sp\sx*size
		For t.trail = Each trail
			If t\par = sp\mesh Then
				If sp\s\spawntimer > 0 Or (sp\s=main_pl And cc_mode = 2) Then 
					t\size = 0
					t\reallen = -1
				Else
					t\size = sp\sx*size*.4
					t\speed = sp\s\zs*2
				EndIf
			EndIf
		Next
	Next
End Function

Function FX_RemoveSparks(s.ship)
	For sp.spark = Each spark
		If sp\s = s Then
			Trail_Remove(sp\mesh)
			Delete sp
		EndIf
	Next
End Function

Function FX_ShieldFX(s.ship, x#,y#,z#, size#)
	mesh = CopyEntity(FX_Shield, s\shield)
	PositionEntity mesh,x,y,z,1
	PointEntity mesh, s\shield
	PositionEntity mesh,0,0,0
	TurnEntity mesh,0,180,Rnd(100)
	;MoveEntity mesh, 0,0,-1
	MoveEntity mesh,Rnd(-.02,.02),Rnd(-.02,.02),Rnd(-.02,.02)
	EntityTexture mesh, teamid[s\team]\shield
	FX_CreateShipFX(FX_FadeOut, s, mesh, size/s\shc\size, Abs(4/size#)+.1)
End Function

Function FX_CreateShipFX.ShipFX(typ,s.ship, mesh, size#, speed#=1)
	f.ShipFX= New ShipFX
	f\s		= s
	f\typ	= typ
	f\mesh	= mesh
	f\size	= size
	f\speed	= speed
	f\anim	= 0
	Return f
End Function

Function FX_FreeShipFX(f.ShipFX)
	FreeEntity f\mesh
	Delete f
End Function

Function FX_RemoveShipFX(s.Ship)
	For f.ShipFX = Each ShipFX
		If f\s = s Then FX_FreeShipFX(f)
	Next
End Function

Function FX_UpdateShipFX()
	For f.ShipFX = Each ShipFX
		firstRound = (f\anim = 0)
		Select f\typ
		Case FX_FadeOut
			EntityAlpha f\mesh,Util_MinMax((100.0-f\anim) / 50.0, 0 ,1)
			TurnEntity f\mesh,0,0,Sin(f\speed)/2.0*main_gspe
		Case FX_ScaleDown
			s# = f\size * (100-f\anim) / 100.0
			ScaleEntity f\mesh,s,s,s
		End Select
		f\anim = f\anim + f\speed*main_gspe
		If Main_CriticalFPS > 0.5 Then
			TFormPoint 0,0,0,f\mesh, cc_cam
			If TFormedZ()>-20 And TFormedZ()<1500 / Main_CriticalFPS Then
				ShowEntity f\mesh
			Else
				HideEntity f\mesh
			EndIf
		EndIf
		If (f\anim > 100) And firstRound=0 Then
			FX_FreeShipFX(f)
		EndIf
	Next
End Function

Function FX_CreateWarp(s.ship,reverse=0)
	w.warp	= New warp
	w\s		= s
	w\mesh	= CopyMesh(FX_Warp,s\piv)
	w\time	= 1000
	w\size	= s\shc\size
	
	EntityParent w\mesh,0
	If reverse Then TurnEntity w\mesh,0,180,0
	MoveEntity w\mesh,0,0,-2
	
	TurnEntity w\mesh,0,0,Rand(360)
	EntityColor w\mesh,teamid[s\team]\colr,teamid[s\team]\colg,teamid[s\team]\colb
	EntityBlend w\mesh,3
	
	ScaleEntity w\mesh,.001,.001,.001
	
	EmitSound FX_WarpSFX,w\mesh
	
	Return w\mesh
End Function

Function FX_UpdateWarps()
	For w.warp = Each warp
		TurnEntity w\mesh,0,0,.1*main_gspe
		w\time = w\time - main_mscleft*.75 - main_mscleft/w\size
		If w\time > 800 ; expand
			size# = (1000 - w\time)*w\size/4000
			ScaleEntity w\mesh,size,size,size/2
		ElseIf w\time <= 0 ; disapear
			FreeEntity w\mesh
			Delete w
		Else ;collaps
			size# = (w\time)*w\size/16000
			ScaleEntity w\mesh,size,size,size/2
		EndIf
	Next
End Function

Function FX_Fake3dSound(snd,x#,y#,z#,vol#)
	;s.sfx = New sfx
	
	dist# = Util_CoordinateDistance(x,y,z,EntityX(cc_cam,1),EntityY(cc_cam,1),EntityZ(cc_cam,1))
	
	If dist/vol < 1500
		piv = CreatePivot()
		PositionEntity piv,x,y,z
		PointEntity piv,cc_cam
		If vol > 1 Then vol=1
		t# = 1.06/vol
		If t < 1.01 Then t=1.01
		MoveEntity piv,0,0,dist/t
		
		Aud_CreateSFX(snd,piv)
		
		FreeEntity piv 
		
		;chan = PlaySound(snd)
		;ChannelPan chan,x/1400.0
		;vol = vol*Float(600-dist)/1300.001
		;ChannelPitch chan,Rand(1500,8000)
		;
		;ChannelVolume chan,vol
	EndIf
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
	
	;chan = EmitSound(fx_explosound[Rand(0,3)],e\mesh)
	;ChannelPitch chan,Rand(8000,22000)
	;ChannelVolume chan,sx/7
	;Return e\mesh
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
	
	FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,.5+sx/120)
	;chan = EmitSound(fx_explosound[Rand(0,3)],e\mesh)
	;ChannelPitch chan,Rand(8000,22000)
	;ChannelVolume chan,sx/7
	;Return e\mesh
End Function

Function FX_CreateExplosion3(x#,y#,z#,sx#=5,sy#=5,r=255,g=255,b=255,endtime=100,scalefact#=1,fadeout=0)
	e.explosion = New explosion
	e\sur		= fx_explo3sur
	e\typ		= 3
	
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
	
	FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,.5+sx/120)
	;chan = EmitSound(fx_explosound[Rand(0,3)],e\mesh)
	;ChannelPitch chan,Rand(8000,22000)
	;ChannelVolume chan,sx/7
	;Return e\mesh
End Function

Function FX_CreateExplosion4(x#,y#,z#,sx#=5,sy#=5,r=255,g=255,b=255,endtime=100,scalefact#=1,fadeout=0)
	e.explosion = New explosion
	e\sur		= fx_explo4sur
	e\typ		= 4
	
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
End Function

Function FX_UpdateExplosions(pause=False)
	Local vert[3]
	
	ClearSurface fx_explosur
	ClearSurface fx_explo2sur
	ClearSurface fx_explo3sur
	ClearSurface fx_explo4sur
	
	Local c# = Float(1)/Float(40)
	;RotateEntity fx_explosion, EntityPitch(fx_explosion,1),EntityYaw(fx_explosion,1),0,1
	;RotateEntity fx_explosion2, EntityPitch(fx_explosion2,1),EntityYaw(fx_explosion2,1),0,1
	;RotateEntity fx_explosion3, EntityPitch(fx_explosion3,1),EntityYaw(fx_explosion3,1),0,1
	
	For e.explosion = Each explosion
		If pause = 0 Then e\time	= e\time + main_gspe
		
		If main_dedicate = 0 Then 
			TFormPoint e\x,e\y,e\z,0,fx_explosion
			x#	= TFormedX()
			y#	= TFormedY()
			z#	= TFormedZ()-0.2-(Abs(e\sx)-Abs(e\sy))/2
			
			If e\scalefact<>0 And pause=0
				fact# = e\scalefact^main_gspe
				e\sx = e\sx * fact#
				e\sy = e\sy * fact#
				e\tx = e\tx * fact#
				e\ty = e\ty * fact#
			EndIf
			
			If e\fadeout = 1
				e\a = 1.5-1.5*Float(e\time)/Float(e\endtime)
				e\a = Util_MinMax(e\a,0,1)
			EndIf
			
			If e\typ = 2 And map_atmo = 1 Then e\y = e\y - Abs(e\sx)*main_gspe/100.0
			
			If z > 0
				Select e\typ
				Case 1
					p# = Float(Int(39*Float(e\time#/Float(e\endtime))))/Float(40)
					vert[0] = AddVertex(e\sur,x+e\sx,y+e\sy,z,p,0) ; ol
					vert[1] = AddVertex(e\sur,x+e\tx,y+e\ty,z,p+c,0) ; or
					vert[2] = AddVertex(e\sur,x-e\tx,y-e\ty,z,p,1) ; ul
					vert[3] = AddVertex(e\sur,x-e\sx,y-e\sy,z,p+c,1) ; ur
				Case 2,3
					e\scalefact = e\scalefact * .9999 ^ main_gspe
					vert[0] = AddVertex(e\sur,x+e\sx,y+e\sy,z,0,0) ; ol
					vert[1] = AddVertex(e\sur,x+e\tx,y+e\ty,z,1,0) ; or
					vert[2] = AddVertex(e\sur,x-e\tx,y-e\ty,z,0,1) ; ul
					vert[3] = AddVertex(e\sur,x-e\sx,y-e\sy,z,1,1) ; ur
				Case 4
					p=1
					e\scalefact = e\scalefact * .99997 ^ main_gspe
					vert[0] = AddVertex(e\sur,x+e\sx*p,y+e\sy*p,z,0,0) ; ol
					vert[1] = AddVertex(e\sur,x+e\tx*p,y+e\ty*p,z,1,0) ; or
					vert[2] = AddVertex(e\sur,x-e\tx*p,y-e\ty*p,z,0,1) ; ul
					vert[3] = AddVertex(e\sur,x-e\sx*p,y-e\sy*p,z,1,1) ; ur
				End Select
				
				VertexColor e\sur,vert[0],e\r,e\g,e\b,e\a
				VertexColor e\sur,vert[1],e\r,e\g,e\b,e\a
				VertexColor e\sur,vert[2],e\r,e\g,e\b,e\a
				VertexColor e\sur,vert[3],e\r,e\g,e\b,e\a
				
				AddTriangle e\sur,vert[0],vert[1],vert[2]
				AddTriangle e\sur,vert[2],vert[1],vert[3]
			EndIf
		EndIf
		
		If e\time >= e\endtime Then Delete e.explosion
	Next
End Function


Function FX_LoadFXSig(path$,parent,s.ship)
	Util_CheckFile(path$)
	mesh = CreatePivot(parent)
	stream = ReadFile(path)
	Repeat
		lin$ = ReadLine(stream)
		Select lin
		Case "sprite{"
			FX_ParseFXSigEntity(stream,mesh,s.ship)
		End Select
	Until Eof(stream)
	CloseFile stream
	Return mesh
End Function

Function FX_ParseFXSigEntity(stream,par,s.ship)
	Repeat
		lin$ = ReadLine(stream)
		If Util_GetParas(lin$)
			Select paras[0]
			Case "x"
				x# 		= paras[1]
			Case "y"
				y# 		= paras[1]
			Case "z"
				z# 		= paras[1]
			Case "sx"
				sx#		= paras[1]
			Case "sy"
				sy#		= paras[1]
			Case "type"
				Typ		= paras[1]
			Case "blend"
				blend	= paras[1]
			Case "alpha"
				alpha#	= paras[1]
			Case "r"
				r#		= paras[1]
			Case "g"
				g#		= paras[1]
			Case "b"
				b#		= paras[1]
			Case "Trail"
				trail	= paras[1]
			End Select
		EndIf
	Until lin="}"
	ent = CopyEntity(fx_ents[typ],par)
	ShowEntity ent
	PositionEntity ent,x,y,z
	ScaleSprite ent,sx,sy
	EntityBlend ent,blend
	EntityAlpha ent,alpha
	EntityColor ent,r,g,b
	If s <> Null
		sp.spark = New spark
		sp\s	 = s
		sp\mesh	 = ent
		sp\sx	 = sx
		sp\sy	 = sy
		
		Trail_Assign(sp\mesh,sp\mesh,5+sp\sx,255,255,255,sp\sx*.4,1+25*(main_pl<>s))
	EndIf
End Function