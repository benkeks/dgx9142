; CubicEnvironmentMapping Library
; by INpac

; RenderModi
; wichtig: dieser Index ist jetzt einfachmal der Index der ComboBox im AES... allerdings ist dort
; planicX der Index 0 ... hier wird aber jedes Obj auf rmode überprüft, ob er größer 0 ist!
; > beim laden der Obj-Data wird +1 !
Const RMOD_PLANICX	= 1
Const RMOD_PLANICY	= 2
Const RMOD_PLANICZ	= 3
Const RMOD_CENTRAL	= 4
Const RMOD_STATICMAP= 128		; dann wird NICHT gerendert!
Const CUBIC_RENDERRANGE	= 10000

Global cub_cam
Global cub_curCamSize
Global cub_ShowCam

Function Cub_InitCubic()

	cub_cam	= CreateCamera()
	cub_ShowCam	= True
	cub_curCamSize	= 256
	CameraViewport cub_cam,0,0,cub_curCamSize,cub_curCamSize
	CameraClsColor cub_cam, 255,255,255
	CameraRange cub_cam, 1,CUBIC_RENDERRANGE
	Cub_HideCubeCam
	
End Function

Function Cub_CreateCubic( o.obj, staticMap$,blendmode,cubemode )		; erstellt abhängig der Eigenschaften des Objs das Cubic-Stuff

	nSize		= o\cub_size
	If Len(staticMap) > 0 Then
		o\cub_rmode	= RMOD_STATICMAP
		o\cub_tex	= LoadTexture( staticMap,128 )
		If o\cub_tex = 0 Then o\cub_rmode = 0 : Return False
	Else
		o\cub_tex	= CreateTexture( nSize,nSize,128+256 )
	EndIf

	EntityTexture o\entity, o\cub_Tex,0,4
	If cubemode Then SetCubeMode o\cub_Tex, cubemode
	If blendmode Then TextureBlend o\cub_Tex, blendmode

End Function


Function Cub_ParseCubic(Stream,o.obj)

	o\cub_size	= 128
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(Trim(paras[0]))
			Case "staticmap"
				Staticmap$	= paras[1]
				o\cub_rMode	= RMOD_STATICMAP
			Case "size"
				o\cub_size	= paras[1]
			Case "rendermode"
				o\cub_rmode	= Int(paras[1])			+1 ; wichtig!
			Case "blendmode"
				cubblendmode= paras[1]
			Case "cubemode"
				cubcubemode	= paras[1]

			End Select
		EndIf
	Until lin="}"

	If o\cub_rMode Then Cub_CreateCubic( o,Staticmap$,cubblendmode,cubcubemode )

End Function



; Functionen zum Rendern oder Updaten einer Cubemap
Function Cub_UpdateMap( o.obj )
If o = Null Then Return False
If o\cub_rmode=0 Or o\cub_rmode = RMOD_STATICMAP Then Return False

	Cub_SetupCubeCam( o )
	Cub_RenderMap( o\cub_tex,o\entity,o\cub_size )
	
End Function

Function Cub_SetupCubeCam( o.obj )


	nxtSize = o\cub_size
	If cub_curCamSize <> nxtSize Then
		CameraViewport cub_cam, 0,0,nxtSize,nxtSize
		;CameraClsMode cub_cam, False,True
		cub_curCamSize	= nxtSize
	EndIf

	Select o\cub_rMode
		Case RMOD_PLANICX
			PositionEntity cub_cam, o\px - ( cam_glx - o\px ), cam_gly, cam_glz
		Case RMOD_PLANICY
			PositionEntity cub_cam, cam_glx, o\py - ( cam_gly - o\py ), cam_glz
		Case RMOD_PLANICZ
			PositionEntity cub_cam, cam_glx, cam_gly, o\pz - ( cam_glz - o\pz )
		Case RMOD_CENTRAL
			PositionEntity cub_cam, o\px,o\py,o\pz
	End Select

	
End Function

; verhindert stetiges ShowEntity durch überprüfen, ob es bereits geshowed wurde
Function Cub_ShowCubeCam()

	If cub_ShowCam = 0 Then
		cub_ShowCam	= 1
		ShowEntity cub_cam
	EndIf
	
End Function
Function Cub_HideCubeCam()

	If cub_ShowCam = 1 Then
		cub_ShowCam	= 0
		HideEntity cub_cam
	EndIf
	
End Function

Function Cub_RenderMap( texture,entity,tex_sz )
If entity+texture = 0 Then Return False

	EntityParent cam_skysprite,cub_cam,0
	tex = texture
		Cub_ShowCubeCam
		HideEntity entity
		CameraClsMode cub_cam,False,True
	
	SetCubeFace tex,0
		RotateEntity cub_cam,0,90,0
		RenderWorld
		;Cub_WaterEffect "hor"
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
	SetCubeFace tex,1
		RotateEntity cub_cam,0,0,0
		RenderWorld
		;Cub_WaterEffect "ver"
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
	SetCubeFace tex,2
		RotateEntity cub_cam,0,-90,0
		RenderWorld
		;Cub_WaterEffect "hor"
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
	SetCubeFace tex,3
		RotateEntity cub_cam,0,180,0
		RenderWorld
		;Cub_WaterEffect "ver"
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
	SetCubeFace tex,4
		RotateEntity cub_cam,-90,0,0
		RenderWorld
		;Cub_WaterEffect "ver"
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
	SetCubeFace tex,5
		RotateEntity cub_cam,90,0,0
		RenderWorld
		;Cub_WaterEffect"hor"
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	
		ShowEntity entity
		Cub_HideCubeCam

	EntityParent cam_skysprite,cam_cam,0
End Function

Function Cub_WaterEffect( dir$ = "hor", steps=4,strength=1,offset1=100,offset2=200 )


	Select dir$
		Case "hor"
			For y = 0 To cub_curCamSize
				y = y + steps
				val = Sin(y+MilliSecs()/60.0)*10
				CopyRect 0,y,main_width,strength,	val,y
			Next
		Case "ver"
			For x = 0 To cub_curCamSize
				x = x + steps
				val = Sin(y+MilliSecs()/60.0)*10
				CopyRect x,0,strength,main_height,	x,val
			Next
	End Select

	DebugLog "end"

End Function