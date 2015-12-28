Type gate
	Field name$,num
	Field destination, range, g.gate
	Field mesh,mesh2,tunnel
	Field pulse#
	Field maps
End Type

Global gate_mesh, gate_hole, gate_tunnel

Function Gate_Init()
	gate_mesh = LoadMeshA(gfxd+"MISC/jumpgate/gate.aem")
	HideEntity gate_mesh
	
	gate_hole = LoadMeshA(gfxd+"MISC/jumpgate/wormhole.b3d")
	EntityBlend gate_hole,3
	EntityFX gate_hole,1+16
	EntityColor gate_hole,20,40,120
	HideEntity gate_hole
	
	gate_tunnel = CreateCylinder(16)
	RotateMesh gate_tunnel,90,0,0
	PositionMesh gate_tunnel,0,0,1
	EntityFX gate_tunnel,1
	HideEntity gate_tunnel
End Function

Function Gate_ParseGate(stream,mesh)
	g.gate = New gate
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(paras[0])
				Case "name",main_lang+"name"
					g\name		= paras[1]
				Case "num","id"
					g\num		= paras[1]
				Case "destination", "to"
					g\destination=paras[1]
				Case "range"
					g\range		= paras[1]
			End Select
		EndIf
	Until lin="}"
	
	g\mesh	= CopyEntity(gate_mesh)
	g\mesh2 = CopyEntity(gate_hole,g\mesh)
	ShowEntity g\mesh
	ScaleEntity g\mesh,g\range/40.0,g\range/4.0,g\range/40.0
	
	;EntityType g\mesh,map_colli
	
	g\pulse = Rand(360)
	;RotateMesh g\mesh,90,0,0
	;f\maps = Hud_AddMinimapObject(f\mesh,f\mesh,0,100)
	
	Return g\mesh
End Function

Function Gate_GetReady()
	For g.gate = Each gate
		If main_texdetail=2 Then
			EntityFX g\mesh,1
			If main_dot3=1 Then
				EntityTexture g\mesh,env_normal,0,0
				EntityTexture g\mesh,shi_specular2additive,0,2
				EntityTexture g\mesh,shi_specular,0,5
			Else
				EntityTexture g\mesh,shi_specular2,0,2
				EntityTexture g\mesh,shi_specular,0,3
			EndIf
		EndIf
		For g2.gate = Each gate
			If g2\num = g\destination Then g\g = g2
		Next
		PointEntity g\mesh,g\g\mesh
	Next
End Function

Function Gate_Update()
	For g.gate = Each gate
		ScaleEntity g\mesh,g\range/40.0,g\range/40.0,g\range/40.0
		If g\tunnel <> 0 And g\pulse > 50 Then FreeEntity g\tunnel g\tunnel = 0
		g\pulse = (g\pulse + main_gspe) Mod 360
		EntityColor g\mesh2,20+Sin(g\pulse)*10,40+Sin(g\pulse)*20,120+Sin(g\pulse)*60
		For sh.ship = Each ship
			If sh\shc\size < 5 And sh\shc\fixed = 0 And sh\spawntimer <=0 And (net=0 Or net_isserver=1 Or main_pl = sh)
				If EntityDistance(sh\piv,g\mesh) < g\range Then
					TFormVector 0,0,1,sh\piv,g\mesh
					If TFormedZ() > .05 Then
						TFormPoint 0,0,0,sh\piv,g\mesh
						If TFormedZ() < 0
							wa = FX_CreateWarp(sh,1)
							
							PositionEntity sh\piv,EntityX(g\g\mesh),EntityY(g\g\mesh),EntityZ(g\g\mesh)
							AlignToVector sh\piv,EntityX(g\g\mesh)-EntityX(g\mesh),EntityY(g\g\mesh)-EntityY(g\mesh),EntityZ(g\g\mesh)-EntityZ(g\mesh),3,.9
							MoveEntity sh\piv,Rnd(-g\g\range/4,g\g\range/4),Rnd(-g\g\range/4,g\g\range/4),g\g\range*1.2
							
							wa = FX_CreateWarp(sh)
							sh\tx = EntityX(wa,1)
							sh\ty = EntityY(wa,1)
							sh\tz = EntityZ(wa,1)
							
							If g\tunnel = 0 Then 
								g\tunnel = CopyEntity(gate_tunnel,g\mesh)
								g\pulse = 0
								ScaleEntity g\tunnel,g\range,g\range,EntityDistance(g\mesh,g\g\mesh)/2,1
							EndIf
							
							If sh = main_pl Then bloom_effect2 = 4
						EndIf
					EndIf
				EndIf
			EndIf
		Next
	Next
End Function

Function Gate_Clear()
	For g.gate = Each gate
		If g\tunnel Then FreeEntity g\tunnel
		FreeEntity g\mesh
		Delete g.gate
	Next
	FreeEntity gate_mesh
	FreeEntity gate_tunnel
	FreeEntity gate_hole
End Function