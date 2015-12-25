Type ship	; Die Schiffe
	Field id
	
	;pos und move
	Field x#,y#,z#
	Field x2d,y2d
	Field tspitch#,tsyaw#
	Field xs#,zs#,zzs#
	Field roll#
	Field afterburner#,burnafter
	Field tx#,ty#,tz#
	Field dx#,dy#,dz#
	Field sx#,sy#,sz#
	Field sx0#,sy0#,sz0#
	Field sx1#,sy1#,sz1#
	Field spitch#,syaw#,sroll#
	Field realspeed#, frontspeed#
	
	;fx
	Field piv
	Field mesh
	Field hmesh
	Field shield, shieldvis#
	Field engine
	Field fxs
	Field mmap
	Field stmap,sumap,aumap
	Field hudhl
	
	;waffen
	Field targeted
	Field weapsel
	Field weapgroup[5]
	Field weapreload#[10]
	Field weapammo[10]
	Field weapammomax[10]
	Field power#
	Field target.ship, targetlock, targeting
	
	;infos
	Field human, nmesh
	Field hitpoints#
	Field shields#
	Field hitby
	Field name$
	Field class,typ
	Field selclass, selspawn
	Field spawntimer
	Field shc.shipclass ; Verlinkung zur Klasse des Schiffes...
	Field team ; Das Team, dem das Schiff angehört.
	Field colr,colg,colb
	Field indanger
	Field supported,stealthed
	Field supporttime#
	Field leavingtheground
	
	;order
	Field order
	Field oship.ship
	Field opiv
	
	;score
	Field points
	Field kills
	Field deaths
End Type

Type shipclass	; Die Schiffsklassen
	Field classid
	Field typ
	Field name$
	Field description$
	Field race$
	
	Field mesh
	Field mini, minitex
	Field mmap,mmapsize#
	Field Size#
	Field CZoom#
	Field engine
	
	Field cloak
	Field Hitpoints
	Field repair#
	Field Shields#
	Field Shieldreload#
	Field power,powerup#
	
	Field SupportRange#
	Field SupportAmmo, SupportAmmoTime
	Field SupportHealth#
	
	Field Stealth
	Field AntiStealth
	
	Field Topspeed#
	Field lowspeed#
	Field Speedup#
	Field speeddown#
	Field slidespeed#
	Field slidespeedup#
	Field Turnspeed#
	Field rollspeedup#
	Field rollinertia#
	Field afterburner#
	Field afterburnertime
	Field Afterburnerreload#
	Field fixed
	
	Field Attackrange
	Field fxsig$
	Field Deathsig
	Field Weapsig
	
	Field MainWeap
	Field MeshData
End Type

Global classid.shipclass[70]

Global main_pl.ship

Global shi_schildmesh, shi_schildtex
Global shi_shieldfx, shi_support

Global shi_specular, shi_specular2, shi_specular2additive

Global shi_aura1, shi_aura2

Global shi_hole


Function Shi_Init()
	Collisions SHI_Colli,SHI_ColliBig,2,2
	Collisions SHI_Colli,MAP_Colli,2,2
	
	Collisions SHI_ColliBig2,MAP_ColliBig2,3,2
	Collisions SHI_ColliBig2,MAP_Colli,2,2
	
	Collisions WEA_Colli,SHI_Colli2,3,2
	Collisions WEA_Colli,SHI_ColliBig,2,2 
	Collisions WEA_Colli,MAP_Colli,2,2
	Collisions WEA_Colli,MAP_ForceField,2,2
	
	shi_schildmesh = CreateSphere(12)
	shi_schildtex = LoadTexture("GFX/MISC/Schild.bmp",64+2+1)
	EntityBlend shi_schildmesh,3
	;EntityFX shi_schildmesh,2+32
	ScaleTexture shi_schildtex,.2,.2
	tbrush = CreateBrush()
	BrushTexture tbrush,shi_schildtex
	PaintMesh shi_schildmesh,tbrush
	FreeBrush tbrush
	
	shi_shieldsfx = Load3DSound("SFX/HIT/shield.mp3")
	shi_support = LoadSound("SFX/GUI/ammo.mp3")
	
	HideEntity shi_schildmesh
	
	shi_aura1 = LoadSprite("GFX/GUI/shield.png",1+2)
	HideEntity shi_aura1
	
	shi_aura2 = LoadSprite("GFX/GUI/aura.png",1+2)
	HideEntity shi_aura2
	
	shi_hole = LoadTexture("GFX/SPRITES/hole.png",1+2+16+32)
End Function

Function Shi_LoadShipClasses() ; Schiffsklassen werden aus "Data/Ships/" geladen
	dir = ReadDir(datad+"SHIPS/")
	NextFile(dir)
	NextFile(dir)
	i = 0
	
	Repeat
		pfad$ = NextFile(dir)
		If pfad$="" Then Exit
		If Right(pfad$,3) = "ini" Then Shi_LoadShipClass(datad+"SHIPS/"+pfad$)
		i = i + 1
	Until pfad$=""
	
	;For i2 = 0 To 20
	;	classid[i2] = Object.shipclass(Shi_FindClassByID(i2))
	;Next
	CloseDir dir
	Return i
End Function

Function Shi_LoadShipClass(pfad$)	; Eine Schiffs-/Stationsklasse wird aus einem File geladen.
	sh.shipclass = New shipclass
	sh\mmapsize = 1
	Util_CheckFile(pfad$)
	stream = ReadFile(pfad$)
	Repeat
		lin$ = ReadLine(stream)
		If Lower(Trim(lin)) = "weapons{" Then
			sh\weapsig = Wea_LoadWeaponSigForShip(stream)
		ElseIf Util_GetParas(lin$)
			Select paras[0]
			Case "Class"
				sh\classid = paras[1]
			Case "Type"
				If paras[1] = "ship"; Or paras[1] = "bigship" Then 
					sh\typ 		= 1
				ElseIf paras[1] = "bomber" Then
					sh\typ 		= 2
				ElseIf paras[1] = "bigship"
					sh\typ		= 3
				ElseIf paras[1] = "cannon"
					sh\typ		= 4
				ElseIf paras[1] = "scout"
					sh\typ		= 5
				ElseIf paras[1] = "support"
					sh\typ		= 6
				ElseIf paras[1] = "cargo"
					sh\typ		= 7
				EndIf
			Case "Name",main_lang+"name"
				sh\Name 		= paras[1]
			Case "Description", main_lang+"Description"
				sh\Description	= paras[1]
			Case "Race"
				sh\Race 		= paras[1]
			Case "Mesh"
				If main_texdetail=2 Then TextureFilter "sphere",64
				sh\Mesh 		= LoadMeshA(paras[1])
				ClearTextureFilters 
				HideEntity sh\mesh
				If main_texdetail=2 Then
					EntityFX sh\mesh,1
					If AEM_ReturnDot3 Then
						EntityTexture sh\mesh,env_normal,0,0
						EntityTexture sh\mesh,shi_specular2additive,0,2
						EntityTexture sh\mesh,shi_specular,0,5
					Else
						EntityTexture sh\mesh,shi_specular2,0,2
						EntityTexture sh\mesh,shi_specular,0,3
					EndIf
				EndIf
			Case "Mini"
				;sh\mini 		= LoadSprite(paras[1],1+2)
				;HideEntity sh\mini
			Case "Mmap"
				sh\mmap 		= LoadSprite(paras[1],1+2)
				HideEntity sh\mmap
			Case "MmapSize"
				sh\mmapsize = paras[1]
			Case "engine"
				sh\engine		= Load3DSound(paras[1])
				LoopSound sh\engine
			Case "Size"
				sh\Size# 		= paras[1]
				sh\czoom		= sh\size/2
			Case "cloak"
				sh\cloak		= paras[1]
			Case "Hitpoints"
				sh\Hitpoints 	= paras[1]
			Case "Shields"
				sh\Shields		= paras[1]
			Case "Shieldreload"
				sh\shieldreload = paras[1]
			Case "Repair"
				sh\repair		= paras[1]
			Case "Topspeed"
				sh\Topspeed		= paras[1]
			Case "LowSpeed"
				sh\LowSpeed		= paras[1]
			Case "Speedup"
				sh\Speedup 		= paras[1]
			Case "SpeedDown"
				sh\SpeedDown 	= paras[1]
			Case "SlideSpeed"
				sh\SlideSpeed 	= paras[1]
			Case "SlideSpeedUp"
				sh\SlideSpeedUp = paras[1]
			Case "Turnspeed"
				sh\Turnspeed# 	= paras[1]
			Case "Rollspeedup"
				sh\rollspeedup	= paras[1]
			Case "Rollinertia"
				sh\rollinertia	= paras[1]
			Case "fixed"
				sh\fixed		= paras[1]
			Case "Afterburner"
				sh\afterburner	= paras[1]
			Case "AfterburnerTime"
				sh\afterburnertime=paras[1]
			Case "AfterburnerReload"
				sh\afterburnerreload=paras[1]
			Case "Power"
				sh\power		= paras[1]
			Case "PowerUp"
				sh\powerup		= paras[1]
			Case "SupportRange"
				sh\supportrange	= paras[1]
			Case "SupportAmmo"
				sh\supportammo	= paras[1]
			Case "SupportAmmoTime"
				sh\SupportAmmoTime = paras[1]
			Case "SupportHealth"
				sh\SupportHealth = paras[1]
			Case "Stealth"
				sh\Stealth		= paras[1]
			Case "AntiStealth"
				sh\AntiStealth	= paras[1]
			Case "Attackrange"
				sh\Attackrange 	= paras[1]
			Case "FXSig"
				sh\fxsig		= paras[1]
			Case "WeaponsigID"
				sh\weapsig 		= paras[1]
			Case "MainWeap"
				sh\mainweap		= paras[1]
			Case "CameraZoom"
				sh\czoom		= paras[1]
			End Select
		EndIf
	Until Eof(stream)
	CloseFile stream
	
	ScaleSprite sh\mmap,sh\mmapsize*8,sh\mmapsize*8
	classid[sh\classid] = sh
	
	If sh\size > 7 Then
		sh\meshdata = CreateBank(1)
		pos = 0
		For surf = 1 To CountSurfaces(sh\mesh)
			s = GetSurface(sh\mesh,surf)
			ResizeBank sh\meshdata, pos + CountTriangles(s)*3*3*4
			For t = 0 To CountTriangles(s)-1
				For v = 0 To 2
					vert = TriangleVertex(s,t,v)
					PokeFloat sh\meshdata, pos+t*9*4+v*12, VertexX(s,vert)+VertexNX(s,vert)
					PokeFloat sh\meshdata, pos+t*9*4+v*12+4, VertexY(s,vert)+VertexNY(s,vert)
					PokeFloat sh\meshdata, pos+t*9*4+v*12+8, VertexZ(s,vert)+VertexNZ(s,vert)
				Next
			Next
			pos = pos + CountTriangles(s)*3*4
		Next
	EndIf
	
	sh\mini = Util_CreateSprite(0)
	HideEntity sh\mini
	sh\minitex = CreateTexture(256,256,1+2+8+256)
	cam = CreateCamera()
	CameraClsColor cam,248,0,248
	SetBuffer BackBuffer()
	CameraViewport cam,0,0,256,256
	size# = Sqr(MeshDepth(sh\mesh)^2+MeshWidth(sh\mesh)^2+MeshHeight(sh\mesh)^2)/2.5
	PositionEntity cam,5999.5-size#*1.2,6000.5+size#*1.2,6000
	PositionEntity sh\mesh,6000,6000,6000+3+size#*2
	RotateEntity sh\mesh,0,130,0
	PointEntity cam,sh\mesh
	HideEntity cc_cam
	ShowEntity sh\mesh
	RenderWorld
	FreeEntity cam
	ShowEntity cc_cam
	HideEntity sh\mesh
	PositionEntity sh\mesh,0,0,0
	RotateEntity sh\mesh,0,0,0
	CopyRect 0,0,256,256,0,0, BackBuffer(), TextureBuffer(sh\minitex)
	LockBuffer TextureBuffer(sh\minitex)
	If main_bit = 16
		For x = 0 To 255
			For y = 0 To 255
				If ReadPixelFast(x,y,TextureBuffer(sh\minitex))= $F0F000F0 Then
					WritePixelFast x,y,0, TextureBuffer(sh\minitex)
				EndIf
			Next
		Next
	Else
		For x = 0 To 255
			For y = 0 To 255
				If ReadPixelFast(x,y,TextureBuffer(sh\minitex))= $FFF800F8 Then
					WritePixelFast x,y,0, TextureBuffer(sh\minitex)
				EndIf
			Next
		Next
	EndIf
	UnlockBuffer TextureBuffer(sh\minitex)
	EntityTexture sh\mini, sh\minitex
	ScaleMesh sh\mini,10,10,1
	EntityFX sh\mini, 1+8
	
	Return sh\classid
End Function


Function Shi_CreateShip(x#,y#,z#,class,name$,team,ki,typ=1,shields=0,hitpoints=0,id=-1)	; Erstellt ein(e) Schiff/Station
	s.ship = New ship
	
	If id = -1
		For i = 1 To 100
			If ships(i) = Null Then
				ships.ship(i)	= s.ship
				s\id		= i
				id			= i
				Exit
			EndIf
		Next
	Else
		If ships(id) <> Null Then Shi_DeleteShip(Handle(ships(id)))
		ships.ship(id)	= s.ship
		s\id		= id
	EndIf
	
	s\x		= x
	s\y		= y
	s\z		= z
	
	
	s\hitby = -1
	
	s\name$	= name$
	s\typ = typ
	Team_JoinTeam(s,team)
	
	s\hudhl = CopyEntity(hud_nextta,cc_cam)
	
	Shi_SelectClass(Handle(s),class,typ)
	
	If ki=1 Then KI_AddKIPlayer(Handle(s.ship))
	If ki=2 Then KI_AddKIPlayer(Handle(s.ship))
	
	s\spawntimer = Rand(1,2)*5000
	If main_pl = s Then
		Hud_SetMode(1)
	EndIf
	
	;If s.ship = hud_t.ship Then hud_t.ship = Null : HUD_ReTarget
	s\shields	= s\shc\shields
	s\hitpoints	= s\shc\hitpoints
	
	If cc_spectating And cc_target = s\piv
		CC_CamSpecChange()
	EndIf
	
	c.class = Race_GetShipsClass(s\shc\classid,s\team,s\typ)
	If c\tickets >= 7 Then HUD_AddIShip(s)
	If typ = 1 Then HUD_AddPlayer(s.ship)
	If hud_commandermode Then HUD_CAddShip(s.ship)
	
	If net_isserver=1 Then Shi_SendCreateShip(s.ship)
	
	Return Handle(s.ship)
End Function

Function Shi_SendCreateShip(s.ship)
	AddUDPByte(C_CreateShip)
	AddUDPByte(s\id)
	AddUDPFloat(s\x)
	AddUDPFloat(s\y)
	AddUDPFloat(s\z)
	AddUDPByte(s\selclass)
	AddUDPString(s\name)
	AddUDPByte(s\team)
	AddUDPByte(s\typ)
	AddUDPShort(s\shields)
	AddUDPShort(s\hitpoints)
	AddUDPByte(s\selspawn)
	AddUDPInteger(s\spawntimer)
End Function

Function Shi_GetCreateShip()
	id = ReadUDPByte()
	
	x# = ReadUDPFloat()
	y# = ReadUDPFloat()
	z# = ReadUDPFloat()
	class = ReadUDPByte()
	name$ = ReadUDPString()
	team = ReadUDPByte()
	typ = ReadUDPByte()
	shields = ReadUDPShort()
	hitpoints = ReadUDPShort()
	selspawn = ReadUDPByte()
	spawntimer = ReadUDPInteger()
	If ships(id) = Null Then 
		s.ship = Object.ship(Shi_CreateShip(x#,y#,z#,class,name$,team,ki,typ,shields,hitpoints,id))
		s\selspawn = selspawn
		s\spawntimer = spawntimer
		If s\spawntimer <= 0
			If s\selclass <> 0 And s\selspawn <> 0
				Race_Equip(Handle(s),s\team,s\selclass,s\typ)
				If main_pl = s Then Hud_SetMode(0)
				s\mmap = Hud_AddMiniMapObject(s\piv,s\shc\mmap,0,s\shc\mmapsize*50+150)
				Hud_MColor s\mmap,s\colr,s\colg,s\colb
				If s\shc\stealth Then
					s\stmap = Hud_AddMiniMapObject(s\piv,shi_aura1,0,s\shc\stealth,1)
					EntityOrder s\stmap,1
					Hud_MColor s\stmap,128,0,255,.4
				EndIf
				If s\shc\supportrange Then
					s\sumap = Hud_AddMiniMapObject(s\piv,shi_aura1,0,s\shc\supportrange,1)
					EntityOrder s\sumap,1
					Hud_MColor s\sumap,200,200,10,.4
				EndIf
				If s = main_pl Then
					s\aumap = Hud_AddMiniMapObject(s\piv,shi_aura2,0,250,2)
					EntityOrder s\aumap,2
					Hud_MColor s\aumap,250,250,250,.7
				EndIf
				ShowEntity s\piv
				AlignToVector s\piv,-s\x,-s\y,-s\z,3
				
				s\power = s\shc\power / 2
				s\roll		= 0
				s\tsyaw		= 0
				s\tspitch	= 0
				s\xs		= 0
				s\zs		= 0
				
				s\tx = s\x
				s\ty = s\y
				s\tz = s\z
			EndIf
		EndIf
	EndIf
	If typ = 2 And selspawn <> 0 And s <> Null Then 
		sp.spawn = Fla_FindSpawnByID(s\selspawn)
		If sp <> Null Then
			sp\s = s
			If spawntimer <= 0 And sp\f\backup <> 0 And sp\class = 1 Then EntityAlpha sp\f\backup,1
		EndIf
	EndIf
End Function

Function Shi_SelectClass(sHandle,class,typ=1)
	s.ship = Object.ship(shandle)
	
	s\selclass = class
	s\class	= class
	class	= Race_GetClass(s\selclass,s\team,typ)
	s\shc.shipclass = classid[class]
	s\typ	= typ
	
	FX_RemoveSparks(s)
	FX_RemoveShipFX(s)
	Cloak_Remove(s)
	Tur_Remove(s)
	
	If shields <> 0 Then s\shields = shields Else s\shields = s\shc\shields
	If hitpoints <> 0 Then s\hitpoints = hitpoints Else s\hitpoints = s\shc\hitpoints
	
	t=0
	If s\piv Then
		If GetParent(cc_piv) = s\piv Then t = 1 : EntityParent cc_piv,0
		FreeEntity s\piv
		HUD_RemoveFromMiniMap(s\piv,s\mmap)
		s\mmap = 0
		s\stmap = 0
		s\sumap = 0
		s\aumap = 0
	EndIf
	s\piv	= CreatePivot()
	s\mesh	= CopyEntity(s\shc\mesh,s\piv)
	If s\shc\size > 7 Then
		s\hmesh = CreateMesh(s\mesh)
		CreateSurface(s\hmesh)
		EntityTexture s\hmesh, shi_hole
	ElseIf s\hmesh<>0 Then
		s\hmesh = 0
	EndIf
	
	s\shield= CreatePivot(s\mesh)
	
	s\targetlock = CreatePivot(s\mesh)
	
	EntityRadius s\piv,MeshDepth(s\mesh)*.75
	
	swidth#	= MeshWidth(s\mesh)
	sheight#= MeshHeight(s\mesh)
	sdepth#	= MeshDepth(s\mesh)
	GetMeshExtents(s\mesh)
	EntityBox s\piv, -swidth/2, -sheight/2,-sdepth/2, swidth, sheight, sdepth
	ScaleEntity s\shield,swidth*.8,sheight*.8,sdepth*.8
	PositionEntity s\shield,(Mesh_MaxX+Mesh_MinX)*.5,(Mesh_MaxY+Mesh_MinY)*.5,(Mesh_MaxZ+Mesh_MinZ)*.5
	
	If s\shc\fxsig<>"" Then s\fxs = FX_LoadFXSig(s\shc\fxsig,s\mesh,s)
	
	PositionEntity s\piv,s\x,s\y,s\z
	If t = 1 Then EntityParent cc_piv,s\piv
	
	makecollbox(s\mesh)
	
	If s\shc\size < 6 And s\shc\fixed=0 Then
		EntityType s\piv, shi_colli
		EntityType s\mesh, shi_colli2
		EntityPickMode s\mesh,3
	Else
		If s\shc\fixed = 0 Then 
			EntityType s\piv, shi_collibig2
		Else
			EntityType s\piv,0
		EndIf
		EntityType s\mesh, shi_collibig
		EntityPickMode s\mesh,2
	EndIf
	
	If s\shc\cloak Then Cloak_CreateCloak(s)
	Race_Equip(Handle(s),s\team,s\selclass,typ)
	Tur_ApplyTurrets(s)
	
	If s = main_pl Then HUD_SetPlayer()
	
	HideEntity s\piv
End Function

Function Shi_SetHuman(s.ship)
	s\human = 1
	If s\nmesh Then FreeEntity s\nmesh
	s\nmesh = Txt_Text(s\name, Hud_font, cc_cam)
	EntityColor s\nmesh,s\colr,s\colg,s\colb
	EntityOrder s\nmesh,-7
End Function

Function Shi_DeleteShip(sHandle,fin=0)	; Löscht ein Schiff bzw. eine Station
	s.ship = Object.ship(shandle)
	
	If s\nmesh And cc_cam Then FreeEntity s\nmesh s\nmesh = 0
	
	If net_isserver = 1 Then Shi_SendDeleteShip(s)
	
	ships(s\id) = Null
	
	For s2.ship = Each ship
		If s2\target = s Then
			s2\target = Null
			If s2 = main_pl Then Hud_ChangeTarget(0)
		EndIf
	Next
	
	If fin=0 Then FreeEntity s\hudhl
	
	FX_RemoveSparks(s)
	FX_RemoveShipFX(s)
	Cloak_Remove(s)
	HUD_RemovePlayer(s)
	Tur_Remove(s)
	
	For k.kiplayer = Each kiplayer
		If k\tars = s Then k\globaction = 0
		If k\sh = s Then Delete k
	Next
	
	For sho.shoot = Each shoot
		If sho\par = s Then
			sho\par = Null
			If sho\swc\typ = 5
				Trail_Remove(sho\mesh)
				Delete sho.shoot
			EndIf
		EndIf
	Next
	
	If s\piv Then
		HUD_RemoveFromMiniMap(s\piv,s\mmap)
		s\mmap = 0
		FreeEntity s\mesh
		FreeEntity s\piv
	EndIf
	
	del = 0
	For is.IShip = Each IShip
		MoveEntity is\name,0,del*20,0
		If is\s = s Then 
			FreeEntity is\name
			Delete is
			hud_iscount = hud_iscount - 1
			del = del + 1
		EndIf
	Next
	
	For s2.ship = Each ship
		If s2\oship = s Then
			s2\order = 0
			s2\oship = Null
		EndIf
	Next
	
	Delete s.ship
	
	If fin = 0 Then
		Hud_CResetShips()
	EndIf
End Function

Function Shi_SendDeleteShip(s.ship)
	AddUDPByte(C_DeleteShip)
	AddUDPByte(s\id)
End Function

Function Shi_GetDeleteShip()
	id = ReadUDPByte()
	s.ship	= ships.ship(id)
	If s <> Null Then
		Shi_DeleteShip(Handle(s))
	EndIf
End Function

Function Shi_ChangeName(s.ship,name$)
	HUD_PrintLog(s\name+" is now known as "+name,0,150,0)
	s\name = name
	If s = main_pl Then net_name = name
	If s\human Then Shi_SetHuman(s.ship)
	HUD_ChangePlayerName(s.ship)
End Function

Function Shi_Reset()
	For s.ship = Each ship
		sp.Spawn = Fla_FindSpawnByID(s\selspawn)
		t = 0
		s\order = 0
		s\oship = Null
		If s\opiv Then FreeEntity s\opiv
		s\opiv = 0
		If sp <> Null And (net=0 Or net_isserver=1)
			If s = sp\s Then
				Shi_DeleteShip(Handle(s))
				sp\s = Null
				t = 1
			EndIf
		ElseIf s\selspawn = 255 And (net=0 Or net_isserver=1)
			Shi_DeleteShip(Handle(s))
			t = 1
		EndIf
		If t = 0
			s\spawntimer = Rand(1,2)*5000
			If main_pl = s Then
				Hud_SetMode(1)
			EndIf
			
			s\shields	= s\shc\shields
			s\hitpoints	= s\shc\hitpoints
			
			If cc_target = s\piv
				CC_CamSpecChange()
			EndIf
			
			HUD_RemoveFromMiniMap(s\piv,s\mmap)
			s\mmap = 0
		EndIf
	Next
End Function

Function Shi_Clear()
	For s.ship = Each ship
		Shi_DeleteShip(Handle(s.ship),1)
	Next
	For i = 0 To 100
		ships(i) = Null
	Next
	FreeEntity shi_schildmesh
	FreeTexture shi_schildtex
	FreeSound shi_shieldfx
	FreeSound shi_support
	FreeEntity shi_aura1
	FreeEntity shi_aura2
	FreeTexture shi_hole
End Function

Function Shi_ClearClasses()
	For sh.shipclass = Each shipclass
		FreeEntity sh\mesh
		FreeEntity sh\mini
		FreeTexture sh\minitex
		FreeEntity sh\mmap
		If sh\meshdata Then FreeBank sh\meshdata
		FreeSound sh\engine
		Delete sh
	Next
End Function

Function Shi_UpdateShips()	; Updated alle Schiffe und Stationen
	RotateTexture shi_schildtex,(MilliSecs() Mod 1000)/2.7777
	tr# = .995 ^ main_gspe
	For s.ship = Each ship
		s\indanger = 1000000
		
		If s\spawntimer > 0
			s\target = Null
			If s\spawntimer = 1 Or net = 0 Or net_isserver=1 Or s = main_pl Then s\spawntimer = s\spawntimer - Util_MinMax(main_mscleft,2,1000000)
			HideEntity s\piv
			If s\nmesh Then HideEntity s\nmesh
			
			If net = 1 And net_isserver = 0
				s\sx = 0
				s\sy = 0
				s\sz = 0
				s\sx1 = 0
				s\sy1 = 0
				s\sz1 = 0
				s\sx0 = s\x
				s\sy0 = s\y
				s\sz0 = s\z
			EndIf
			
			If s\spawntimer <= 0
				If s\selclass <> 0 And s\selspawn <> 0
					sp.spawn = Fla_FindSpawnByID(s\selspawn)
					launchship = 0
					
					If s\selspawn = 255 Then
						launchship = 1
					ElseIf sp\f\team <> s\team Or sp\f\takeper < 90 And (s = main_pl Or net=0 Or net_isserver = 1)
						If sp\s = s
							If net_isserver=1 Or net=0 Then
								Shi_DeleteShip(Handle(s))
								sp\s = Null
							EndIf
						Else
							s\selspawn = 0
							s\spawntimer = 5000 
							Shi_SendSpawnData(s)
						EndIf
					Else
						launchship = 1
					EndIf
					
					If launchship
						Race_Equip(Handle(s),s\team,s\selclass,s\typ)
						If net = 0 Or net_isserver = 1 Or s = main_pl
							If s\selspawn <> 255
								PositionEntity s\piv,EntityX(sp\piv,1)+Rand(-sp\range,sp\range),EntityY(sp\piv,1)+Rand(-sp\range,sp\range),EntityZ(sp\piv,1)+Rand(-sp\range,sp\range)
							EndIf
						EndIf
						If main_pl = s Then Hud_SetMode(0)
						s\mmap = Hud_AddMiniMapObject(s\piv,s\shc\mmap,0,s\shc\mmapsize*50+150,0,s\shc\mmapsize*8)
						Hud_MColor s\mmap,s\colr,s\colg,s\colb
						If s\shc\stealth Then
							s\stmap = Hud_AddMiniMapObject(s\piv,shi_aura1,0,s\shc\stealth,1)
							Hud_MColor s\stmap,128,0,255,.4
						EndIf
						If s\shc\supportrange Then
							s\sumap = Hud_AddMiniMapObject(s\piv,shi_aura1,0,s\shc\supportrange,1)
							Hud_MColor s\sumap,200,200,10,.4
						EndIf
						If s = main_pl Then
							s\aumap = Hud_AddMiniMapObject(s\piv,shi_aura2,0,250,2)
							EntityOrder s\aumap,-2
							Hud_MColor s\aumap,250,250,250,.7
						EndIf
						ShowEntity s\piv
						
						s\x = EntityX(s\piv)
						s\y = EntityY(s\piv)
						s\z = EntityZ(s\piv)
						If s\shc\size < 30 Then
							AlignToVector s\piv,-s\x,-s\y,-s\z,3
							If s\selspawn <> 255
								If sp\pitch = -9999
									RotateEntity s\piv,EntityPitch(s\piv), EntityYaw(s\piv), 0
								Else
									RotateEntity s\piv,sp\pitch, sp\yaw, sp\roll
								EndIf
							EndIf
							wa = FX_CreateWarp(s)
							s\dx = (s\x-EntityX(wa,1))*.5
							s\dy = (s\y-EntityY(wa,1))*.5
							s\dz = (s\z-EntityZ(wa,1))*.5
						Else
							If s\selspawn <> 255
								If sp\pitch = -9999
									RotateEntity s\piv,EntityPitch(s\piv), EntityYaw(s\piv), 0
								Else
									RotateEntity s\piv,sp\pitch, sp\yaw, sp\roll
								EndIf
							EndIf
							s\tx = s\x
							s\ty = s\y
							s\tz = s\z
							s\dx = 0
							s\dy = 0
							s\dz = 0
						EndIf
						
						ResetEntity s\piv
						
						If s = main_pl Then bloom_effect2 = 4
						
						s\power	 	= s\shc\power / 2
						s\afterburner= s\shc\afterburnertime
						s\roll		= 0
						s\tsyaw		= 0
						s\tspitch	= 0
						s\xs		= 0
						s\zs		= 0
						
						s\leavingtheground = 0
						
						If s\selspawn <> 255
							If sp\s = s And sp\f\backup <> 0 And sp\class = 1 Then EntityAlpha sp\f\backup,1
						EndIf
						
						If s = main_pl Or net_isserver=1 Then
							s\spawntimer = 1
							If net_isserver Then Shi_SendPosition(s) ElseIf net=1 Then Shi_SendCPosition(s) 
							Shi_SendSpawnData(s)
							s\spawntimer = -1
						EndIf
						If s\hmesh Then ClearSurface(GetSurface(s\hmesh,1))
					EndIf
				Else
					s\spawntimer = 5000 
				EndIf
			ElseIf s\selclass <> 0 And s\selspawn <> 0 And s\selspawn <> 255
				sp.spawn = Fla_FindSpawnByID(s\selspawn)
				If sp\s = s And sp\f\backup <> 0 And sp\class = 1 Then
					If MilliSecs() Mod 1000 < 500
						EntityAlpha sp\f\backup,0.8
					Else
						EntityAlpha sp\f\backup,0.2
					EndIf
				EndIf
			EndIf
			
		Else
			If s\shc\fixed = 0 Then teamid[s\team]\shipcount = teamid[s\team]\shipcount + 1
			
			;lod
			TFormPoint 0,0,0,s\mesh,cc_cam
			If (cc_mode = 6 And s\shc\size < 4) Or (cc_mode = 2 And s = main_pl)
				EntityAlpha s\mesh,0
			ElseIf TFormedZ()/s\shc\size>450 Then
				EntityAlpha s\mesh,0
			Else
				EntityAlpha s\mesh,1
			EndIf
			
			s\x = EntityX(s\piv)
			s\y = EntityY(s\piv)
			s\z = EntityZ(s\piv)
			
			If net<>0 And net_isserver = 0 And s <> main_pl
				For i = 1 To Ceil(main_gspe)
					s\sx = s\sx *.95 + s\sx1*.05
					s\sy = s\sy *.95 + s\sy1*.05
					s\sz = s\sz *.95 + s\sz1*.05
				Next
				
				TranslateEntity s\piv,s\sx*main_gspe,s\sy*main_gspe,s\sz*main_gspe
				RotateEntity s\piv,EntityPitch(s\piv)+s\spitch,EntityYaw(s\piv)+s\syaw,s\sroll
				s\realspeed = Util_CoordinateDistance(s\x,s\y,s\z,s\tx,s\ty,s\tz)
				s\tx = s\x
				s\ty = s\y
				s\tz = s\z
				s\dx = s\sx
				s\dy = s\sy
				s\dz = s\sz
			Else
				If map_gravi > 0.0001 And s\shc\fixed = 0 ; gravitation
					If Abs(s\zzs) < map_gravi / s\shc\size
						s\dy = s\dy - (map_gravi / s\shc\size - Abs(s\zzs)) * .05 * main_gspe
					EndIf
				EndIf
				
				If s\shc\size < 5 And s\shc\fixed=0 ; kollision
					For i = 1 To CountCollisions(s\piv)
						s2.ship = Shi_FindShipByMesh(CollisionEntity(s\piv,i))
						Local t# = 0.0
						If s2 <> Null Then
							t = (Abs((s2\dx-s\dx)*CollisionNX(s\piv,i) + (s2\dy-s\dy)*CollisionNY(s\piv,i) + (s2\dz-s\dz)*CollisionNZ(s\piv,i)))
							If t < 1 Then t = 0
							If  net=0 Or net_isserver=1 Then
								s\hitpoints = s\hitpoints - 40*t*s\shc\size
								s2\hitpoints = s2\hitpoints - 40*t*s\shc\size
							EndIf
						Else
							t = Abs(s\dx*CollisionNX(s\piv,i)+s\dy*CollisionNY(s\piv,i)+s\dz*CollisionNZ(s\piv,i))
							If t < 1 Then t = 0
							If net=0 Or net_isserver=1 Then s\hitpoints = s\hitpoints - 40*t*s\shc\size
						EndIf
						If t > 1
							For i2 = 0 To 1
								fx_createexplosion2( CollisionX(s\piv,i)+Rnd(-1,1), CollisionY(s\piv,i)+Rnd(-1,1), CollisionZ(s\piv,i)+Rnd(-1,1),t*Rnd(2,3),t*Rnd(2,3),Rnd(230,255),Rnd(110,200),Rnd(90),Rand(100,200),Rnd(1.02,1.025),1)
							Next
						EndIf
						s\dx = s\dx*.9 + CollisionNX(s\piv,i)*t/2
						s\dy = s\dy*.9 + CollisionNY(s\piv,i)*t/2
						s\dz = s\dz*.9 + CollisionNZ(s\piv,i)*t/2
					Next
				EndIf
				
				s\tx = s\x
				s\ty = s\y
				s\tz = s\z
				
				If s\afterburner < s\shc\afterburnertime
					s\afterburner = s\afterburner + s\shc\afterburnerreload * main_gspe
				Else
					s\afterburner = s\shc\afterburnertime
				EndIf
				
				If s\burnafter 
					If s\afterburner <= 0
						s\burnafter = 0
						If s = main_pl Then Bloom_MB = 0 
					Else
						s\afterburner = s\afterburner - main_gspe
						If s = main_pl Then
							Bloom_MB = 1
							cc_quake = cc_quake + .12
						EndIf
					EndIf
				EndIf
				
				If s\xs > s\shc\slidespeed Then s\xs = s\shc\slidespeed
				If s\xs < -s\shc\slidespeed Then s\xs = -s\shc\slidespeed
				If s\zzs > s\shc\topspeed Then s\zzs = s\shc\topspeed
				If s\zzs < s\shc\lowspeed Then s\zzs = s\shc\lowspeed
				
				s\tsyaw		= s\tsyaw * .98^main_gspe
				s\tspitch	= s\tspitch * .98^main_gspe
				s\xs		= s\xs * .99^main_gspe
				s\zs		= s\zs - (s\zs-s\zzs)
				s\roll		= s\roll * s\shc\rollinertia^main_gspe
				TurnEntity s\piv, s\tspitch*main_gspe*2,s\tsyaw*main_gspe*2,s\roll*main_gspe
				RotateEntity s\mesh, s\tspitch*30,0,s\tsyaw#*40-s\xs*130 + s\roll*20
				
				If s\shc\fixed = 0
					TFormVector s\xs*main_gspe, 0, s\zs*main_gspe+s\burnafter*s\shc\afterburner*main_gspe, s\piv, 0
					s\dx = s\dx*tr + TFormedX()/25.0
					s\dy = s\dy*tr + TFormedY()/25.0
					s\dz = s\dz*tr + TFormedZ()/25.0
					TranslateEntity s\piv,(s\dx)*main_gspe,(s\dy)*main_gspe,(s\dz)*main_gspe
				Else
					s\dx = 0
					s\dy = 0
					s\dz = 0
				EndIf
				
				s\realspeed = Sqr(s\dx*s\dx+s\dy*s\dy+s\dz*s\dz)
				TFormVector s\dx,s\dy,s\dz, 0,s\piv
				s\frontspeed = TFormedZ()
			EndIf
			
			
			If s\power < s\shc\power Then s\power = s\power + s\shc\powerup * main_gspe
			If s\power > s\shc\power Then s\power = s\shc\power
			
			If s\shields <= s\shc\shields Then s\shields = s\shields + s\shc\shieldreload * main_gspe
			If s\hitpoints <= s\shc\hitpoints Then s\hitpoints = s\hitpoints + s\shc\repair * main_gspe
			
			If s\shields > s\shc\shields Then s\shields = s\shc\shields
			If s\hitpoints > s\shc\hitpoints Then s\hitpoints = s\shc\hitpoints
			
			If ChannelPlaying(s\engine) = 0
				If EntityDistance(s\mesh,cc_cam) < 900
					s\engine = EmitSound(s\shc\engine,s\piv)
					ChannelVolume s\engine, .5+.5*Abs(s\zs/s\shc\topspeed)
				EndIf
			ElseIf EntityDistance(s\mesh,cc_cam) < 900
				ChannelVolume s\engine, .5+.5*Abs(s\zs/s\shc\topspeed)
			Else
				StopChannel s\engine
			EndIf
			
			;If s\fxs Then EntityAlpha s\fxs,Abs(s\zs)/s\shc\topspeed
			
			If s\targeting < 100 Then s\targeting = 0
			For i = 1 To 10
				twg = weapsigi(s\shc\weapsig,i,1)
				If twg<>0 And s\weapgroup[twg]<>0
					If s\weapammo[twg] >= weaponid[s\weapgroup[twg]]\neAmmo Then
						s\weapreload[i] = s\weapreload[i] - main_gspe
					Else
						s\weapreload[i] = weaponid[s\weapgroup[twg]]\reload
					EndIf
					If s\weapreload[i] <= 0 Then s\weapreload[i] = 0
					
					If weaponid[s\weapgroup[twg]]\target<>0 And s\weapammo[twg]>0 And s\targeting < 100 Then s\targeting = weaponid[s\weapgroup[twg]]\targetspeed
				EndIf
			Next
			
			If s\target = Null
				s\targeting = 0
			ElseIf s\targeting >= 100 Then
				PointEntity s\targetlock, s\target\piv
				TFormPoint 0,0,0,s\target\piv,s\mesh
				If TFormedZ() / (Abs(TFormedY())+Abs(TFormedX())) < 0.5 Then
					s\targeting = 0
				EndIf
			ElseIf s\targeting>0 And s\target<>Null Then 
				TFormPoint 0,0,0,s\target\piv,s\mesh
				If TFormedZ() / (Abs(TFormedY())+Abs(TFormedX())) > 0.5 Then
					dy# = DeltaYaw(s\targetlock,s\target\piv)
					dp# = DeltaPitch(s\targetlock,s\target\piv)
					If Abs(dp)+Abs(dy) > 1 Then
						If Abs(Sgn(dy) * s\targeting * main_gspe / 10.0) < Abs(dy) Then dy = Sgn(dy) * s\targeting * main_gspe / 10.0
						If Abs(Sgn(dp) * s\targeting * main_gspe / 10.0) < Abs(dp) Then dp = Sgn(dp) * s\targeting * main_gspe / 10.0
						RotateEntity s\targetlock, EntityPitch(s\targetlock,1)+dp, EntityYaw(s\targetlock,1)+dy, 0, 1
					Else
						s\targeting = 100
					EndIf
				Else
					RotateEntity s\targetlock,0,0,0
				EndIf
			Else
				RotateEntity s\targetlock,0,0,0
			EndIf
			
			; namensanzeige
			If s\nmesh Then 
				ShowEntity s\nmesh
				PositionEntity s\nmesh,EntityX(s\piv),EntityY(s\piv),EntityZ(s\piv),1
				fa# = EntityDistance(s\nmesh,cam_cam)
				If fa# <> 0.00 Then PositionEntity s\nmesh,EntityX(s\nmesh)*356/fa,EntityY(s\nmesh)*356/fa,EntityZ(s\nmesh)*356/fa
			EndIf
			
			; shields
			If s\shieldvis>0
				:ShowEntity s\shield
				;EntityAlpha s\shield,0;Float(s\shieldvis#)/Float(50.00)
				s\shieldvis	= s\shieldvis - .6 * main_gspe
			Else
				
				;HideEntity s\shield
			EndIf
			
			; update the support-state
			If s\supported = 1
				s\supported = 2
			ElseIf s\supported = 2
				s\supported = 0
			EndIf
			
			; hide tarned ships on radar
			If s\mmap
				Select s\stealthed
				Case 1
					If s\team <> main_pl\team
						EntityAlpha s\mmap,0
						Hud_MColor s\mmap,s\colr,s\colg,s\colb,0
					Else
						EntityAlpha s\mmap,.4
						Hud_MColor s\mmap,s\colr,s\colg,s\colb,.4
					EndIf
					s\stealthed = 2
				Case 2
					s\stealthed = 0
				Case -1
					s\stealthed = -2
					EntityAlpha s\mmap,1
					Hud_MColor s\mmap,s\colr,s\colg,s\colb,1
				Case -2
					s\stealthed = 0
				Case 0
					EntityAlpha s\mmap,1
					Hud_MColor s\mmap,s\colr,s\colg,s\colb,1
				End Select
			EndIf
			
			; How does the ship affect other ships?
			If s\shc\supportrange<>0 Or s\shc\stealth<>0
				If s\shc\stealth <> 0 Then s\stealthed = 1 ; scouts tarnen sich selbst
				If s\stmap Then EntityAlpha s\stmap,(s\team = main_pl\team)*.5
				If s\sumap Then EntityAlpha s\sumap,(s\team = main_pl\team)*.5
				tdist# =  s\shc\supportrange
				ts.ship = Null
				For s2.ship = Each ship
					If s\team = s2\team And s<>s2 And s2\spawntimer < 0
						dist = EntityDistance(s\piv,s2\piv)
						If dist <= tdist
							tdist	= dist
							ts		= s2
						EndIf
						
						If dist < s\shc\supportrange
							s2\supported = 1
							If s2\shields <= s2\shc\shields Then s2\shields = s2\shields + s\shc\shieldreload * main_gspe * s\shc\supporthealth
							If s2\hitpoints <= s2\shc\hitpoints Then s2\hitpoints = s2\hitpoints + s\shc\repair * main_gspe * s\shc\supporthealth
							If s2\shields > s2\shc\shields Then s2\shields = s2\shc\shields
							If s2\hitpoints > s2\shc\hitpoints Then s2\hitpoints = s2\shc\hitpoints
						EndIf
						
						If dist < s\shc\stealth
							If s2\stealthed >= 0 And s2\shc\size < 10 Then s2\stealthed = 1
						EndIf
					EndIf
				Next
				If ts <> Null And s\supporttime <= 0 And (net_isserver = 1 Or net=0)
					s2 = ts
					t = 0
					For i = 0 To 4
						twg = weapsigi(s2\shc\weapsig,i,1)
						t2 = 1
						If weaponid[s2\weapgroup[i]] <> Null Then t2 = weaponid[s2\weapgroup[i]]\reammo
						If t2 = 0 Then s2\weapammomax[i] = s2\weapammo[i]
						If s2\weapammo[i] < s2\weapammomax[i]
							If s2\weapammomax[i] < 20 Then
								fact = 1
							ElseIf s2\weapammomax[i] < 50
								fact = 2
							ElseIf s2\weapammomax[i] < 100
								fact = 3
							ElseIf s2\weapammomax[i] < 150
								fact = 4
							Else 
								fact = 5
							EndIf
							s2\weapammo[i] = s2\weapammo[i] + s\shc\supportammo * fact
							If s2\human=1 And net_isserver=1 Then Shi_SendAmmo(s2, i)
							t = 1
						EndIf
						If s2\weapammo[i] > s2\weapammomax[i] Then s2\weapammo[i] = s2\weapammomax[i]
					Next
					If t=1 And s2 = main_pl Then PlaySound shi_support
					s\supporttime = s\shc\supportammotime
				EndIf
			EndIf
			
			s\supporttime = s\supporttime - main_mscleft
			
			; Has this ship left the battle ground?
			
			If Util_CoordinateDistance(0,0,0, s\x,s\y,s\z) > map_radius Then
				s\leavingtheground = s\leavingtheground + main_mscleft
				If s\leavingtheground > 15000 And (net=0 Or net_isserver=1) Then s\hitpoints = s\hitpoints - 20*main_gspe
			Else 
				s\leavingtheground = 0
			EndIf
			
			; commands
			If s\oship <> Null Then 
				If s\oship\spawntimer > 0 Then
					s\oship = Null
					s\order = ORDER_STOP
				EndIf
			EndIf
			
			; damage
			If s\hitpoints < s\shc\hitpoints*.5 And Rnd(0,2/Abs(s\zzs))<=3 Then
				x# = EntityX(s\piv)
				y# = EntityY(s\piv)
				z# = EntityZ(s\piv)
				fx_createexplosion4(x+Rnd(-3,3),y+Rnd(-3,3),z+Rnd(-3,3),Rnd(2,3),Rnd(2,3),Rnd(160,230),Rnd(150,220),Rnd(150,220),Rand(450,600),Rnd(1.006,1.009),1)
				;fx_createexplosion(x+Rnd(-3,3),y+Rnd(-3,3),z+Rnd(-3,3),Rnd(7,9),Rnd(7,9),Rnd(160,230),Rnd(150,220),Rnd(150,220),Rand(450,650))
			EndIf 
			
			If net_isserver And net_update Then
				Shi_SendPosition(s)
			ElseIf net=1 And net_update=1 And main_pl = s
				Shi_SendCPosition(s)
			EndIf
			
			;CameraProject cc_cam,EntityX(s\piv),EntityY(s\piv),EntityZ(s\piv)
			;Text ProjectedX(),ProjectedY(),s\id
			
			; death
			
			If s\hitpoints<=0 And (net = 0 Or net_isserver = 1)
				
				hitter.ship = Null
				If s\hitby>=0 Then hitter.ship = ships(s\hitby)
				If hitter <> Null
					HUD_PrintLog(hitter\name,hitter\colr,hitter\colg,hitter\colb,10000,1)
					HUD_WriteLog(" [kills] ",200,200,200,10000,1)
					HUD_WriteLog(s\name,s\colr,s\colg,s\colb,10000,1)
					hitter\kills = hitter\kills + 1
					hitter\points = hitter\points + 1
				Else
					HUD_PrintLog(s\name,s\colr,s\colg,s\colb,10000,1)
					HUD_WriteLog(" [destroyed]",200,200,200,10000,1)
				EndIf
				s\deaths = s\deaths + 1
				;s\points = s\points - 1
				
				; explosions
				x# = EntityX(s\piv)
				y# = EntityY(s\piv)
				z# = EntityZ(s\piv)
				si# = 1
				For i = 0 To Ceil(s\shc\size/5)
					shi_explode(x,y,z,si#,s\shc\size/6+.8)
					x# = Rnd(-MeshWidth(s\mesh)/3,MeshWidth(s\mesh)/3)
					y# = Rnd(-MeshHeight(s\mesh)/3,MeshHeight(s\mesh)/3)
					z# = Rnd(-MeshDepth(s\mesh)/3,MeshDepth(s\mesh)/3)
					TFormPoint x,y,z,s\piv,0
					x = TFormedX()
					y = TFormedY()
					z = TFormedZ()
					si#= s\shc\size/5+.4+Rnd(-0.5,1.2)
				Next
				x# = EntityX(s\piv)
				y# = EntityY(s\piv)
				z# = EntityZ(s\piv)
				If s\shc\size >=5
					For i2 = 1 To s\shc\size/8
						x# = Rnd(-MeshWidth(s\mesh)/3,MeshWidth(s\mesh)/3)
						y# = Rnd(-MeshHeight(s\mesh)/3,MeshHeight(s\mesh)/3)
						z# = Rnd(-MeshDepth(s\mesh)/3,MeshDepth(s\mesh)/3)
						TFormPoint x,y,z,s\piv,0
						x = TFormedX()
						y = TFormedY()
						z = TFormedZ()
						fx_createexplosion3(x,y,z,0,0,355,355,355,Rand(150,200),Rand(100,150))
						fx_createexplosion3(x,y,z,0,0,355,355,355,Rand(150,200),Rand(100,150))
					Next
				EndIf
				
				Wreck_Create.wreck(s)
				
				;set spawn timer
				sp.spawn = Fla_FindSpawnByID(s\selspawn)
				If sp <> Null
					s\spawntimer = sp\spawnt
					If s = sp\s And sp\spawnonce = 1 Then s\spawntimer = 200000000
				Else
					s\spawntimer = 5000
				EndIf
				If main_pl = s And Game_RestartTimer < MilliSecs() Then
					Hud_SetMode(1)
				EndIf
				
				For s2.ship = Each ship
					If s2\target = s Then
						s2\target = Null
						If s2 = main_pl Then Hud_Retarget
					EndIf
				Next
				s\shields	= s\shc\shields
				s\hitpoints	= s\shc\hitpoints
				
				If cc_target = s\piv
					CC_CamSpecChange()
				EndIf
				
				HUD_RemoveFromMiniMap(s\piv,s\mmap)
				s\mmap = 0
				s\stmap = 0
				s\sumap = 0
				s\aumap = 0
				
				;s\order = ORDER_STOP
				;s\oship = Null
				
				c.class = Race_GetShipsClass(s\shc\classid,s\team,s\typ)
				teamid[s\team]\tickets = teamid[s\team]\tickets - c\tickets
				If hitter<>Null Then
					t = c\tickets
					If t > 10 Then t = 10 + t / 100
					hitter\points = hitter\points - 1 + t
				EndIf
				
				If teamid[s\team]\tickets + c\tickets > 0 And teamid[s\team]\tickets <= 0 Then
					CC_SetTarget(s\piv,5)
				EndIf
				If net_isserver
					Shi_SendDeath(s)
					Shi_SendScore(s)
					If hitter <> Null Then Shi_SendScore(hitter)
					If s\human = 1 Then s\spawntimer = 200000
				EndIf
				s\hitby = -1
				HideEntity s\piv
				
				If s\typ = 3 Then Shi_DeleteShip(Handle(s))
				;Shi_SendSpawnData(s.ship)
			EndIf
		EndIf
	Next
End Function

Function Shi_SetTarget(s.ship,target.ship)
	If s\target <> target Then
		s\targeting = 0
		s\target = target
	EndIf
End Function

Function Shi_HitShip(s.ship, x#,y#,z#, power#)
	Local vert[4]
	If s\shc\size > 4 And s\shc\meshdata <> 0 Then
		surf = GetSurface(s\hmesh,1)
		If CountVertices(surf) < 10000*main_detail
			For i = 0 To BankSize(s\shc\meshdata)-36 Step 36
				t = 0
				For i2 = 0 To 35 Step 12
					If Abs(PeekFloat(s\shc\meshdata,i+i2)-x)<power 
						If Abs(PeekFloat(s\shc\meshdata,i+i2+4)-y)<power 
							If Abs(PeekFloat(s\shc\meshdata,i+i2+8)-z)<power 
								t = 1
								Exit
							EndIf
						EndIf
					EndIf
				Next
				If t Then 
					For i2 = 0 To 2
						x1# = PeekFloat(s\shc\meshdata,i+i2*12)
						y1# = PeekFloat(s\shc\meshdata,i+i2*12+4)
						z1# = PeekFloat(s\shc\meshdata,i+i2*12+8)
						vert[i2] = AddVertex(surf,x1,y1,z1, ((Abs(x1-x)+Abs(z1-z))/power)/4.0+.5,(y1-y)/power/2+.5)
					Next
					AddTriangle(surf,vert[0],vert[1],vert[2])
				EndIf
			Next
		EndIf
	EndIf
End Function

Function Shi_SendPosition(s.ship) ; 27 bytes ... 18 bytes ... 12 bytes! :)
	AddUDPByte(C_PositionShip)
	AddUDPByte(s\id)
	
	;AddUDPFloat(s\x)
	;AddUDPFloat(s\y)
	;AddUDPFloat(s\z)
	
	s\x = EntityX(s\piv)
	s\y = EntityY(s\piv)
	s\z = EntityZ(s\piv)
	
	;x# = s\x
	;y# = s\y
	;z# = s\z
 	;pit# = EntityPitch(s\piv)
	;ya# = EntityYaw(s\piv)
	;ro# = EntityRoll(s\piv)
	
	piv = CreatePivot(s\piv)
	; extrapolieren
	
	For i = 0 To 15
		TurnEntity piv, s\tspitch*net_rate/65,s\tsyaw*net_rate/65,s\roll*net_rate/65
		TranslateEntity piv, s\dx*net_rate/260,s\dy*net_rate/260,s\dz*net_rate/260
	Next
	
	sx# = EntityX(piv,1)
	sy# = EntityY(piv,1)
	sz# = EntityZ(piv,1)
	;senden
		
	AddUDPShort(sx*2+32000)
	AddUDPShort(sy*2+32000)
	AddUDPShort(sz*2+32000)
	
	;AddUDPFloat(s\tx)
	;AddUDPFloat(s\ty)
	;AddUDPFloat(s\tz)
	
	;AddUDPFloat(s\tspitch)
	;AddUDPFloat(s\tsyaw)
	
	;AddUDPFloat(s\zzs)
	;AddUDPByte(s\burnafter)
	
	AddUDPByte((EntityPitch(piv,1)+90)*255.0/180)
	AddUDPByte((EntityYaw(piv,1)+180)*255.0/360)
	AddUDPByte((EntityRoll(piv,1)+180)*255.0/360)
	
	AddUDPByte(s\zs*200+50)
	
	FreeEntity piv
	
	; extrapolation rückgängig machen
	;PositionEntity s\piv,x,y,z
	;RotateEntity s\piv,pit,ya,ro
	
	;s\x = EntityX(s\piv)
	;s\y = EntityY(s\piv)
	;s\z = EntityZ(s\piv)
				
	;AddUDPFloat(EntityPitch(s\piv))
	;AddUDPFloat(EntityYaw(s\piv))
	;AddUDPFloat(EntityRoll(s\piv))

End Function

Function Shi_GetPosition()
	id = ReadUDPByte()
	s.ship	= ships.ship(id)
	If s <> Null And s<>main_pl Then
		;s\sx1	= (ReadUDPFloat()-s\x)/net_stime * 12
		;s\sy1	= (ReadUDPFloat()-s\y)/net_stime * 12
		;s\sz1	= (ReadUDPFloat()-s\z)/net_stime * 12
		
		x# = (ReadUDPShort()-32000)/2
		y# = (ReadUDPShort()-32000)/2
		z# = (ReadUDPShort()-32000)/2
		
		If net_stime < 1 Then net_stime = 1
		
		s\sx1	= (x-s\x)/net_stime * 13
		s\sy1	= (y-s\y)/net_stime * 13
		s\sz1	= (z-s\z)/net_stime * 13
		
		If Util_CoordinateDistance(s\x,s\y,s\z,x,y,z) > 200 Then
			For g.gate = Each gate
				If Util_CoordinateDistance(s\x,s\y,s\z, EntityX(g\mesh),EntityY(g\mesh),EntityZ(g\mesh))<g\range+20
					If Util_CoordinateDistance(x,y,z, EntityX(g\g\mesh),EntityY(g\g\mesh),EntityZ(g\g\mesh))<g\g\range+20
						If g\tunnel = 0 Then 
							g\tunnel = CopyEntity(gate_tunnel,g\mesh)
							g\pulse = 0
							ScaleEntity g\tunnel,g\range,g\range,EntityDistance(g\mesh,g\g\mesh)/2,1
						EndIf
					EndIf
				EndIf
			Next

			s\x = x
			s\y = y
			s\z = z
			s\sx = 0
			s\sy = 0
			s\sz = 0
			s\sx1 = 0
			s\sy1 = 0
			s\sz1 = 0
			PositionEntity s\piv,x,y,z
			ResetEntity s\piv
		EndIf
		
		s\sx0 = s\x
		s\sy0 = s\y
		s\sz0 = s\z
		
		;s\tx	= ReadUDPFloat()
		;s\ty	= ReadUDPFloat()
		;s\tz	= ReadUDPFloat()
		;s\tx	= s\sx - s\dx
		;s\ty	= s\sy - s\dy
		;s\tz	= s\sz - s\dz
		
		;s\spitch= ReadUDPFloat()
		;s\syaw	= ReadUDPFloat()
		;s\sroll	= ReadUDPFloat()
		
		s\spitch= ReadUDPByte()*180.0/255.0-90
		s\syaw= ReadUDPByte()*360.0/255.0-180
		s\sroll= ReadUDPByte()*360.0/255.0-180
		
		If s <> main_pl
			RotateEntity s\piv,EntityPitch(s\piv),EntityYaw(s\piv),s\sroll
			
			piv = CreatePivot(s\piv)
			RotateEntity piv,s\spitch,s\syaw,s\sroll,1
			MoveEntity piv,0,0,1
			s\spitch = DeltaPitch(s\piv,piv)*12.0/net_stime
			s\syaw	 = DeltaYaw(s\piv,piv)*12.0/net_stime
			FreeEntity piv
			
			s\zs = Float(ReadUDPByte()-50)/200.0
		Else 
			ReadUDPByte()
		EndIf
		
		If s\spawntimer > 0 Then s\spawntimer = 1
		;PositionEntity s\piv,s\x,s\y,s\z
		;RotateEntity s\piv,ReadUDPByte()*180.0/256.0-90,ReadUDPByte()*360.0/256.0-180,ReadUDPByte()*360.0/256.0-180
	Else
		ReadUDPShort()
		ReadUDPShort()
		ReadUDPShort()
		ReadUDPByte()
		ReadUDPByte()
		ReadUDPByte()
		ReadUDPByte()
	EndIf
End Function

Function Shi_SendCPosition(s.ship) ; 45 bytes
	If s <> main_pl Then Return
	AddUDPByte(S_Position)
	AddUDPByte(s\id)
	
	AddUDPFloat(EntityX(s\piv))
	AddUDPFloat(EntityY(s\piv))
	AddUDPFloat(EntityZ(s\piv))
	
	AddUDPFloat(s\dx)
	AddUDPFloat(s\dy)
	AddUDPFloat(s\dz)
	
	AddUDPFloat(EntityPitch(s\piv))
	AddUDPFloat(EntityYaw(s\piv))
	AddUDPFloat(EntityRoll(s\piv))
	
	AddUDPFloat(s\roll)
	AddUDPFloat(s\xs)
	AddUDPFloat(s\zs)
	
	AddUDPFloat(s\tspitch)
	AddUDPFloat(s\tsyaw)
	
	AddUDPByte(s\burnafter)
End Function

Function Shi_GetCPosition()
	id = ReadUDPByte()
	s.ship = ships(id)
	If s <> Null
		s\x = ReadUDPFloat()
		s\y = ReadUDPFloat()
		s\z = ReadUDPFloat()
		
		PositionEntity s\piv,s\x,s\y,s\z
		
		s\dx = ReadUDPFloat()
		s\dy = ReadUDPFloat()
		s\dz = ReadUDPFloat()
		
		pit# = ReadUDPFloat()
		ya# = ReadUDPFloat()
		ro# = ReadUDPFloat()
		RotateEntity s\piv,pit,ya,ro
				
		s\roll = ReadUDPFloat()
		s\xs = ReadUDPFloat()
		s\zzs = ReadUDPFloat()
		
		s\tspitch = ReadUDPFloat()
		s\tsyaw = ReadUDPFloat()
		
		s\burnafter = ReadUDPByte()
	Else
		ReadUDPFloat()
		ReadUDPFloat()
		ReadUDPFloat()
		
		ReadUDPFloat()
		ReadUDPFloat()
		ReadUDPFloat()
		
		ReadUDPFloat()
		ReadUDPFloat()
		ReadUDPFloat()
		
		ReadUDPFloat()
		ReadUDPFloat()
		ReadUDPFloat()
		
		ReadUDPFloat()
		ReadUDPFloat()
		
		ReadUDPByte()
	EndIf
End Function

Function Shi_SendDeath(s.ship)
	If net_isserver
		AddUDPByte(C_ShipDeath)
		AddUDPByte(s\id)
		AddUDPByte(s\hitby)
		AddUDPInteger(s\spawntimer)
		AddUDPShort(teamid[s\team]\tickets+12000)
	EndIf
End Function

Function Shi_GetDeath()
	id = ReadUDPByte()
	hitby = ReadUDPByte()
	If hitby = 255 Then hitby = -1
	spawntimer = ReadUDPInteger()
	tickets = ReadUDPShort()-12000
	s.ship	= ships.ship(id)
	If s <> Null Then
		s\spawntimer = spawntimer
		hiiter.ship = Null
		s\hitby = hitby
		If s\hitby>=0 Then hitter.ship = ships(s\hitby)
		If hitter <> Null
			HUD_PrintLog(hitter\name,hitter\colr,hitter\colg,hitter\colb,10000,1)
			HUD_WriteLog(" [kills] ",200,200,200,10000,1)
			HUD_WriteLog(s\name,s\colr,s\colg,s\colb,10000,1)
		Else
			HUD_PrintLog(s\name,s\colr,s\colg,s\colb,10000,1)
			HUD_WriteLog(" [destroyed]",200,200,200,10000,1)
		EndIf
		
		Wreck_Create.wreck(s)
		
		; explosions
		x# = EntityX(s\piv)
		y# = EntityY(s\piv)
		z# = EntityZ(s\piv)
		si# = 1
		For i = 1 To s\shc\size
			shi_explode(x,y,z,si#,s\shc\size/5+.8)
			x# = Rnd(-MeshWidth(s\mesh)/3,MeshWidth(s\mesh)/3)
			y# = Rnd(-MeshHeight(s\mesh)/3,MeshHeight(s\mesh)/3)
			z# = Rnd(-MeshDepth(s\mesh)/3,MeshDepth(s\mesh)/3)
			TFormPoint x,y,z,s\piv,0
			x = TFormedX()
			y = TFormedY()
			z = TFormedZ()
			si#= s\shc\size/2+.5+Rnd(-1.0,1.2)
		Next
		x# = EntityX(s\piv)
		y# = EntityY(s\piv)
		z# = EntityZ(s\piv)
		If s\shc\size >=5
			For i2 = 2 To s\shc\size/4
				;fx_createexplosion(x,y,z,0,0,355,355,355,Rand(150,200),Rand(100,150))
				;fx_createexplosion(x,y,z,0,0,355,355,355,Rand(150,200),Rand(100,150))
				x# = Rnd(-MeshWidth(s\mesh)/3,MeshWidth(s\mesh)/3)
				y# = Rnd(-MeshHeight(s\mesh)/3,MeshHeight(s\mesh)/3)
				z# = Rnd(-MeshDepth(s\mesh)/3,MeshDepth(s\mesh)/3)
				TFormPoint x,y,z,s\piv,0
				x = TFormedX()
				y = TFormedY()
				z = TFormedZ()
				fx_createexplosion(x,y,z,0,0,355,355,355,Rand(150,200),Rand(100,150))
				fx_createexplosion(x,y,z,0,0,355,355,355,Rand(150,200),Rand(100,150))
			Next
		EndIf
		
		;set spawn timer
		If main_pl = s Then
			Hud_SetMode(1)
		EndIf
		
		For s2.ship = Each ship
			If s2\target = s Then
				s2\target = Null
				If s2 = main_pl Then Hud_Retarget()
			EndIf
		Next
		s\shields	= s\shc\shields
		s\hitpoints	= s\shc\hitpoints
		
		If cc_target = s\piv
			CC_CamSpecChange()
		EndIf
		
		HUD_RemoveFromMiniMap(s\piv,s\mmap)
		s\mmap = 0
		s\stmap = 0
		s\sumap = 0
		s\aumap = 0
		
		s\hitby = -1
		
		teamid[s\team]\tickets = tickets
		HideEntity s\piv
	EndIf
End Function

Function Shi_SendScore(s.ship)
	AddUDPByte(C_Score)
	AddUDPByte(s\id)
	AddUDPShort(s\points)
	AddUDPShort(s\kills)
	AddUDPShort(s\deaths)
End Function

Function Shi_GetScore()
	id=ReadUDPByte()
	p = ReadUDPShort()
	k = ReadUDPShort()
	d = ReadUDPShort()
	s.ship	= ships.ship(id)
	If s <> Null Then
		s\points = p
		s\kills = k
		s\deaths = d
	EndIf
End Function

Function Shi_SendAmmo(s.ship,i)
	If net_isserver Then
		AddUDPByte(C_Ammo)
		AddUDPByte(s\id)
		AddUDPByte(i)
		AddUDPShort(Int(Util_MinMax(s\weapammo[i],0,s\weapammomax[i])))
	EndIf
End Function

Function Shi_GetAmmo()
	id	= ReadUDPByte()
	i	= ReadUDPByte()
	ammo= ReadUDPShort()
	
	s.ship	= ships.ship(id)
	DebugLog "Ammo "+id+": "+ammo
	If s <> Null And i <= 10 Then
		s\weapammo[i] = ammo
		If s\weapammo[i] > s\weapammomax[i] Then s\weapammo[i] = s\weapammomax[i]
		If s = main_pl Then PlaySound shi_support
	EndIf
End Function

Function Shi_SendSpawnData(s.ship)
	If net_isserver Then
		AddUDPByte(C_SpawnData)
		AddUDPByte(s\id)
		AddUDPByte(s\selclass)
		AddUDPByte(s\team)
		AddUDPByte(s\selspawn)
		AddUDPInteger(s\spawntimer)
	ElseIf net=1 And s = main_pl
		AddUDPByte(S_SpawnData)
		AddUDPByte(s\id)
		AddUDPByte(s\selclass)
		AddUDPByte(s\team)
		AddUDPByte(s\selspawn)
		AddUDPInteger(s\spawntimer)
	EndIf
End Function

Function Shi_GetSpawnData()
	id = ReadUDPByte()
	s.ship	= ships.ship(id)
	If s <> Null Then
		oclass	= s\selclass
		oteam	= s\team
		ospawn	= s\selspawn
		s\selclass	= ReadUDPByte()
		s\team		= ReadUDPByte()
		s\selspawn	= ReadUDPByte()
		If main_pl = s Then
			ReadUDPInteger()
		Else
			s\spawntimer= ReadUDPInteger()
		EndIf
		DebugLog "spawndata for "+s\name
		Team_JoinTeam(s,s\team,0)
		If (s\team <> oteam Or s\selclass <> oclass) And s\selclass <> 0 Then Shi_SelectClass(Handle(s),s\selclass,s\typ)
		If Net_isserver Then Shi_SendSpawnData(s.ship)
	Else
		ReadUDPByte()
		ReadUDPByte()
		ReadUDPByte()
		ReadUDPInteger()
	EndIf
End Function

Function Shi_Explode(x#,y#,z#,s#=1,t#=1)
	For i = 0 To 2
	fx_createexplosion(x+Rnd(-3,3),y+Rnd(-3,3),z+Rnd(-3,3),s*Rnd(7,9),s*Rnd(7,9),Rnd(230,255),Rnd(100,160),Rnd(60),Rand(70,150)*t)
	Next
	For i = 0 To 1
	fx_createexplosion(x+Rnd(-1,1),y+Rnd(-1,1),z+Rnd(-1,1),s*Rnd(4,5),s*Rnd(4,5),Rnd(200,255),Rnd(150,210),Rnd(99,160),Rand(60,110)*t)
	Next
	For i = 0 To 1
		fx_createexplosion2(x+Rnd(-3,3),y+Rnd(-3,3),z+Rnd(-3,3),s*Rnd(2,3),s*Rnd(2,3),Rnd(230,255),Rnd(110,200),Rnd(90),Rand(100,200),Rnd(1.02,1.025),1)
	Next
	FX_Fake3dSound(fx_explosound[Rand(0,3)],x,y,z,2+s/160)
End Function


Function Shi_SetWeapon(sHandle,group,weapon,ammo)
	s.ship				= Object.ship(shandle)
	s\weapgroup[group]	= weapon
	s\weapammo[group]	= ammo 
	s\weapammomax[group]= ammo 
End Function

Function Shi_Fire(sHandle,typ,target=0)
	s.ship	= Object.ship(shandle)
	
	If net=1 And net_isserver = 0
		AddUDPByte(S_Fire)
		AddUDPByte(s\id)
		AddUDPByte(s\targeting)
		AddUDPByte(typ)
		ts.ship = Object.ship(target)
		If ts <> Null Then
			AddUDPByte(ts\id)
		Else
			AddUDPByte(255)
		EndIf
		Return
	EndIf
	
	id		= s\shc\weapsig
	somethingfired = 0
	For i = 1 To 10
		twg = weapsigi(id,i,1) 
		If (  weapsigi(id,i,0)=typ And s\weapreload[i]<=.1)
			If s\weapammo[twg] >= weaponid[s\weapgroup[twg]]\neAmmo And s\power > weaponid[s\weapgroup[twg]]\NePower And (typ<>3 Or weaponid[s\weapgroup[twg]]\base1 = 0); <=
				tpiv = CreatePivot(s\mesh)
				EntityPickMode tpiv,1
				PositionEntity tpiv,weapsigf(id,i,0),weapsigf(id,i,1),weapsigf(id,i,2)
				x# 	= EntityX(tpiv,1)
				y# 	= EntityY(tpiv,1)
				z# 	= EntityZ(tpiv,1)
				EntityParent tpiv,0
				If typ = 3
					;Geschütz
					dist# = 1001
					For s2.ship = Each ship
						If s2\team <> s\team And s2\spawntimer <= 0
							edist# = EntityDistance(s2\mesh,tpiv)-400*(s2\shc\typ=4)
							If target = Handle(s2) Then edist = edist/3-300
							If edist < dist
								distt#	= edist# / weaponid[s\weapgroup[twg]]\speed#
								TranslateEntity s2\piv,s2\dx*distt#,s2\dy*distt#,s2\dz*distt#
								If EntityVisible(s2\mesh,tpiv)
									dist = edist
									p# = DeltaPitch(tpiv,s2\piv)
									ya# = DeltaYaw(tpiv,s2\piv)
									PointEntity tpiv,s2\piv
									targetd = Handle(s2)
									dist2 = EntityDistance(tpiv,s2\piv)
									;If target = targetd Then dist = dist/3
								EndIf
								TranslateEntity s2\piv,-s2\dx*distt#,-s2\dy*distt#,-s2\dz*distt#
							EndIf
						EndIf
					Next
					If dist < 1000
						If weapsigf(id,i,3) <> 0 Or weapsigf(id,i,4) <> 0 Then
							p = weapsigf(id,i,3)
							ya= weapsigf(id,i,4)
							RotateEntity tpiv,0,0,0
							TurnEntity tpiv,p,ya,0
						EndIf
						p#	= EntityPitch(tpiv,1)
						ya#	= EntityYaw(tpiv,1)
						r#	= EntityRoll(tpiv,1)
						c = wea_count
						Wea_CreateShoot(  x, y, z, p, ya, r, s\weapgroup[ weapsigi(id,i,1) ],targetd,s,dist2, -1,1 )
						s\power = s\power - weaponid[s\weapgroup[twg]]\NePower
						s\weapreload[i] = weaponid[s\weapgroup[twg]]\reload
						s\weapammo[twg] = s\weapammo[twg] - weaponid[s\weapgroup[twg]]\neAmmo
						If net_isserver
							If somethingfired = 0
								AddUDPByte(C_Fire)
								AddUDPByte(s\id)
								AddUDPByte(s\targeting)
								somethingfired = 1
							EndIf
							AddUDPByte(i)
							AddUDPShort(c)
							s2 = Object.ship(targetd)
							AddUDPByte(s2\id)
						EndIf
						Else
						s\weapreload[i] = weaponid[s\weapgroup[twg]]\reload
					EndIf
				Else
					If target
						s2.ship = Object.ship(target)
						edist# = EntityDistance(s2\mesh,tpiv)
						distt#	= edist# / (weaponid[s\weapgroup[twg]]\speed#+s\frontspeed#)
						TranslateEntity s2\piv,s2\dx*distt#,s2\dy*distt#,s2\dz*distt#
						;MoveEntity s2\piv,0,0,s2\realspeed*distt#
						;If EntityVisible(s2\mesh,tpiv)
						p#	= EntityPitch(tpiv)
						ya#	= EntityYaw(tpiv)
						r#	= EntityRoll(tpiv)
						
							RotateEntity tpiv, p,ya,0
							p# = DeltaPitch(tpiv,s2\piv)
							ya# = DeltaYaw(tpiv,s2\piv)
							If Abs(p) + Abs(ya) < weaponid[s\weapgroup[twg]]\aiming Then 
								PointEntity tpiv,s2\piv
							EndIf
						;EndIf
						TranslateEntity s2\piv,-s2\dx*distt#,-s2\dy*distt#,-s2\dz*distt#
						;MoveEntity s2\piv,0,0,-s2\realspeed*distt#
					EndIf
					
					p#	= EntityPitch(tpiv)
					ya#	= EntityYaw(tpiv)
					r#	= EntityRoll(tpiv)
					
					c = wea_count
					Wea_CreateShoot(  x, y, z, p, ya, 0, s\weapgroup[ weapsigi(id,i,1) ],target,s  )
					s\weapreload[i] = weaponid[s\weapgroup[twg]]\reload
					s\weapammo[twg] = s\weapammo[twg] - weaponid[s\weapgroup[twg]]\neAmmo
					s\power = s\power - weaponid[s\weapgroup[twg]]\NePower
					
					If net_isserver
						If somethingfired = 0
							AddUDPByte(C_Fire)
							AddUDPByte(s\id)
							AddUDPByte(s\targeting)
							somethingfired = 1
						EndIf
						AddUDPByte(i)
						AddUDPShort(c)
						s2 = Object.ship(target)
						If s2<>Null Then AddUDPByte(s2\id) Else AddUDPByte(255)
					EndIf
				EndIf
				FreeEntity tpiv
			EndIf
		EndIf
	Next
	If somethingfired Then AddUDPByte(0)
End Function

Function Shi_GetFire()
	sid = ReadUDPByte()
	targeting = ReadUDPByte()
	s.ship = ships(sid)
	If s <> Null Then
		s\targeting = targeting
		Repeat
			s2.ship = Null
			byte = ReadUDPByte()
			If byte = 0 Then Exit
			wid = ReadUDPShort()
			at = ReadUDPByte()
			If at <> 255 Then s2.ship = ships(at)
			mesh = 0
			If s2 <> Null Then mesh = s2\piv target=Handle(s2)
			
			If s <> Null
				i = byte
				id	= s\shc\weapsig
				
				twg = weapsigi(id,i,1) 
				tpiv = CreatePivot(s\mesh)
				EntityPickMode tpiv,1
				PositionEntity tpiv,weapsigf(id,i,0),weapsigf(id,i,1),weapsigf(id,i,2)
				x# 	= EntityX(tpiv,1)
				y# 	= EntityY(tpiv,1)
				z# 	= EntityZ(tpiv,1)
				EntityParent tpiv,0
				If weapsigi(id,i,0) = 3
					;Geschütz
					If s2 <> Null
						edist# = EntityDistance(mesh,tpiv)
						distt#	= edist# / weaponid[s\weapgroup[twg]]\speed#
						TranslateEntity s2\piv,s2\dx*distt#,s2\dy*distt#,s2\dz*distt#
						dist = edist
						PointEntity tpiv,s2\piv
						targetd = Handle(s2)
						dist2 = EntityDistance(tpiv,s2\piv)
						TranslateEntity s2\piv,-s2\dx*distt#,-s2\dy*distt#,-s2\dz*distt#
					EndIf
					
					If weapsigf(id,i,3) <> 0 Or weapsigf(id,i,4) <> 0 Then
						p# = weapsigf(id,i,3)
						ya#= weapsigf(id,i,4)
						RotateEntity tpiv,0,0,0
						TurnEntity tpiv,p,ya,0
					EndIf
					p#	= EntityPitch(tpiv,1)
					ya#	= EntityYaw(tpiv,1)
					r#	= EntityRoll(tpiv,1)
					
					Wea_CreateShoot(  x, y, z, p, ya, r, s\weapgroup[ weapsigi(id,i,1) ],targetd,s,dist2,wid,1  )
					s\power = s\power - weaponid[s\weapgroup[twg]]\NePower
					s\weapreload[i] = weaponid[s\weapgroup[twg]]\reload
					s\weapammo[twg] = s\weapammo[twg] - weaponid[s\weapgroup[twg]]\neAmmo
				Else
					If s2 <> Null
						edist# = EntityDistance(s2\mesh,tpiv)
						distt#	= edist# / (weaponid[s\weapgroup[twg]]\speed#+s\realspeed*.8)
						TranslateEntity s2\piv,s2\dx*distt#,s2\dy*distt#,s2\dz*distt#
						p#	= EntityPitch(tpiv)
						ya#	= EntityYaw(tpiv)
						r#	= EntityRoll(tpiv)
						
						RotateEntity tpiv, p,ya,0
						p# = DeltaPitch(tpiv,s2\piv)
						ya# = DeltaYaw(tpiv,s2\piv)
						If Abs(p) + Abs(ya) < weaponid[s\weapgroup[twg]]\aiming Then
							PointEntity tpiv,s2\piv
						EndIf
						TranslateEntity s2\piv,-s2\dx*distt#,-s2\dy*distt#,-s2\dz*distt#
					EndIf
					
					p#	= EntityPitch(tpiv)
					ya#	= EntityYaw(tpiv)
					r#	= EntityRoll(tpiv)
					
					Wea_CreateShoot(  x, y, z, p, ya, 0, s\weapgroup[ weapsigi(id,i,1) ],target,s,0,wid  )
					s\weapreload[i] = weaponid[s\weapgroup[twg]]\reload
					s\weapammo[twg] = s\weapammo[twg] - weaponid[s\weapgroup[twg]]\neAmmo
					s\power = s\power - weaponid[s\weapgroup[twg]]\NePower
				EndIf
				FreeEntity tpiv
			EndIf
		Forever
	Else
		Repeat
			byte = ReadUDPByte()
			If byte = 0 Then Exit
			wid = ReadUDPShort()
			at = ReadUDPByte()
		Forever
	EndIf
End Function

Function Shi_GetCFire()
	id = ReadUDPByte()
	targeting = ReadUDPByte()
	typ = ReadUDPByte()
	tid = ReadUDPByte()
	
	s.ship = ships(id)
	If s <> Null
		s\targeting = targeting
		If tid <> 255 Then t.ship = ships(tid)
		If t<>Null
			target = Handle(t)
		Else
			target = 0
		EndIf
		Shi_Fire(Handle(s),typ,target)
	EndIf
End Function

 Function Shi_ShieldEffect(mesh,surface,x#,y#,z#) ; function wird eigentlich nicht mehr gebraucht...
	tpiv = CreatePivot(mesh)
	PositionEntity tpiv,x,y,z,1
	x2 = EntityX(tpiv,0)
	y2 = EntityY(tpiv,0)
	z2 = EntityZ(tpiv,0)
	FreeEntity tpiv
	For i = 0 To CountVertices(surface)-1
		alpha# = .2/Util_CoordinateDistance#(VertexX(surface,i),VertexY(surface,i),VertexZ(surface,i),x2,y2,z2)
		VertexColor surface,i,VertexRed(surface,i),VertexGreen(surface,i),VertexBlue(surface,i),alpha#
	Next
End Function


Function Shi_FindClassByID(id)
	For sh.shipclass = Each shipclass
		If sh\classid = id
			Return Handle(sh)
		EndIf
	Next
End Function

Function Shi_FindByMesh(mesh) ; Sucht nach dem Type zu einem Mesh
	For s.ship = Each ship
		If s\mesh = mesh Or s\piv = mesh Then Return Handle(s)
	Next
End Function

Function Shi_FindShipByMesh.ship(mesh) ; Sucht nach dem Type zu einem Mesh
	For s.ship = Each ship
		If s\mesh = mesh Or s\piv = mesh Then Return s
	Next
End Function