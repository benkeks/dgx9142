;Bloom / Glow post-process filter by Bouncer
;Using SSwift's excellent texture blurring routine
;Use at your own risk :) may contain bugs 
;Warning ... this filter uses LOT of fillrate so it's not for the old gfx-cards 

;V1.2
;Fixed camera bug...

Global bloom_effect# = .5
Global bloom_effect2# = .5*.7
Global bloom_mb# = 0
Global bloom_mbmode = 3

Global bloom_pause = 0

; musste einstellen:
Global CameraViewportX	= 0
Global CameraViewportY	= 0;BalkenHeight
Global CameraViewportW	= main_width
Global CameraViewportH	= main_height;_height-balkenHeight*2


;SSwifts blur routine ---------------------
; This is the location in the world where the camera will reside.
; This location should be far from any other geometry you have in your world.
Const BLUR_CAM_X# = 65536.0
Const BLUR_CAM_Y# = -65536.0
Const BLUR_CAM_Z# = 0.0

;Modified to additive blend in the same time as blur.
;Also modified the copyrecting... so that multiple passes don't cause texture drifting.
;this is just a quick fix... don't actually get why tex texture drifts...
Function BlurTexture(Texture, Blur_Quality, Blur_Radius#, nonadditive=0)

; -------------------------------------------------------------------------------------------------------------------
; This function blurs a texture using a technique that takes advantage of 3D acceleration.  
;
; * You MUST hide all other cameras before calling this function!
; * You MUST reset your texture's blending mode, scale, and position after calling this function!
;
; Texture is the texture you want blurred.
;
; Blur_Quality defines the quality of the blur.  1 = 4 passes, 2 = 8 passes, 3 = 12 passes, etc.
;
; (The reason that the passes are in multiples of four is because interference artifacts are created when
; the number of passes is not a multiple of four... meaning that ten passes will actually look far worse
; than eight.)
;
; Blur_Radius# defines the radius of the blur, in pixels, assuming a map size of 256x256.
;
;(Ie, a radius of 16 will be the same width regardless of whether the texture is 16x16 or 512x512.  It will
; only be exactly 16 pixels wide if the map is 256x256.)
; -------------------------------------------------------------------------------------------------------------------

; This is used for temporary storage of the meshes used for soft shadow blurring.
Local BlurMesh[16*4]


; If blurring is enabled...
If Blur_Quality > 0

Blur_Cam = CreateCamera()

; Set the camera's range to be very small so as to reduce the possiblity of extra objects making it into the scene.
CameraRange Blur_Cam, 0.1, 100

; Set the camera to zoom in on the object to reduce perspective error from the object being too close to the camera.
CameraZoom Blur_Cam, 16.0

; Aim camera straight down.
RotateEntity Blur_Cam, 90, 0, 0, True

; Set the camera viewport to the same size as the texture.
CameraViewport Blur_Cam, 0, 0, TextureWidth(Texture), TextureHeight(Texture)

; Set the camera so it clears the color buffer before rendering the texture.
CameraClsColor Blur_Cam, 0,0,0
CameraClsMode  Blur_Cam, True, True

; Position the blur camera far from other entities in the world.
PositionEntity Blur_Cam, BLUR_CAM_X#, BLUR_CAM_Y#, BLUR_CAM_Z#

; Create the sprites to use for blurring the shadow maps.
For Loop = 0 To (Blur_Quality*4)-1
BlurMesh[Loop] = CreateSprite()
EntityBlend BlurMesh[Loop],3
Next

; Scale the texture down because we scale the sprites up so they fill a larger area of the
; screen.  (Otherwise the edges of the texture are darker than the middle because they don't
; get covered.
ScaleTexture    Texture, 0.5, 0.5
PositionTexture Texture, 0.5, 0.5

; Blur texture by blitting semi-transparent copies of it on top of it.
BlurRadius# = Blur_Radius# * (1.0 / 256.0)
BlurAngleStep# = 360.0 / Float(Blur_Quality*4)

; Normally we would just divide 255 by the number of passes so that adding all the passes
; together would not exceed 256.  However, if we did that, then we could not have a number of
; passes which does not divide 256 evenly, or else the error would result in the white part of
; the image being slightly less than white.  So we round partial values up to ensure that
; white will always be white, even if it ends up being a little whiter than white as a result
; when all the colors are added, since going higher than white just clamps to white.
BlurShade = Ceil(255.0 / Float(Blur_Quality*4))

; Place each of the blur objects around a circle of radius blur_radius.
For Loop = 0 To (Blur_Quality*4)-1

EntityTexture BlurMesh[Loop], Texture
EntityFX BlurMesh[Loop], 1+8
If nonadditive Then
	EntityAlpha BlurMesh[Loop], 1.0 / Float(Blur_Quality*4)
Else
	EntityAlpha BlurMesh[Loop], 1.0 / Float(Loop+1)
EndIf
ScaleSprite BlurMesh[Loop], 2, 2

BlurAngle# = BlurAngleStep# * Float(Loop) + 180.0*(Loop Mod 2)

Xoff# = BlurRadius# * Cos(BlurAngle#)
Yoff# = BlurRadius# * Sin(BlurAngle#)

PositionEntity BlurMesh[Loop], BLUR_CAM_X# + Xoff#, BLUR_CAM_Y# - 16.0, BLUR_CAM_Z# + Yoff#, True

Next

; Render the new texture.
RenderWorld

; Copy the new texture from the screen buffer to the texture buffer.
CopyRect 1, 1, TextureWidth(Texture)-1, TextureHeight(Texture)-1, 0, 0, BackBuffer(), TextureBuffer(Texture)

; Free the blur entities.
For Loop = 0 To (Blur_Quality*4)-1
FreeEntity BlurMesh[Loop]
Next

; Free the blur camera.
FreeEntity Blur_Cam

EndIf

End Function
;---------------------------------------------------



;GLOW VARIABLES
Global GL_cam
Global GL_scenecam					;This stores the pointer to your scene camera
Global GL_offset = 65536			;Offset for the glow camera
Global GL_texsize = 256			;Glow texture resolution (128 or 256 recommended)
Global GL_camzoom# = 1.0		;Change this value if your camzoom differs from the default

Global GL_sprite,GL_sprited
Global GL_tex,GL_texbuffer
Global GL_tex2,GL_texbuffer2

Global GL_sprite_scene
Global GL_timer=1

Global GL_renderingtimer = MilliSecs()

;Initializes the filter
;--------------------------

;camera - your main scene camera
;NOTE - do not change camera rotation before calling this init function.
Function InitGlow(camera, campiv, viewportx,viewporty,viewportW,viewportH)
	GL_scenecam = camera
	GL_scenecam_piv = campiv
	
	;SET GLOW CAMERA
	GL_cam = CreateCamera()
	PositionEntity GL_cam,GL_offset,0,0
	CameraProjMode GL_cam,2
	CameraRange GL_Cam, 1, 100
	CameraClsMode GL_cam,0,1
	CameraViewport GL_cam,0,0,GL_texsize,GL_texsize
	HideEntity GL_cam
	
	;SET GLOW SPRITE 1 (FOR MOTIONBLUR)
	GL_sprite = CreateSprite()
	GL_tex2 = CreateTexture(GL_texsize,GL_texsize,256+16+32)
	GL_texbuffer2 = TextureBuffer(GL_tex2)
	EntityTexture GL_sprite,GL_tex2
	ScaleSprite GL_sprite,2.1*(1.0/GL_camzoom),2.1*(1.0/GL_camzoom)
	PositionEntity GL_sprite,EntityX(GL_scenecam),EntityY(GL_scenecam),EntityZ(GL_scenecam)+2
	;PositionEntity GL_sprite,GL_offset,0,1
	EntityOrder GL_sprite,-5.1
	EntityFX GL_sprite,1
	EntityParent GL_sprite,GL_scenecam_piv
	
	;SET GLOW SPRITE 1.5 (FOR DARKENING PASSES)
	GL_sprited = CreateSprite()
	PositionEntity GL_sprited,GL_offset,0,1
	EntityOrder GL_sprited,-5.1
	EntityFX GL_sprited,1
	
	;SET GLOW SPRITE 2  (FINAL  OVERLAY)
	GL_sprite_scene = CreateSprite()
	GL_tex = CreateTexture(GL_texsize,GL_texsize,256+16+32)
	GL_texbuffer = TextureBuffer(GL_tex)
	EntityTexture GL_sprite_scene,GL_tex
	ScaleSprite GL_sprite_scene,2*(1.0/GL_camzoom),2*(1.0/GL_camzoom)
	PositionEntity GL_sprite_scene,EntityX(GL_scenecam),EntityY(GL_scenecam),EntityZ(GL_scenecam)+2
	EntityParent GL_sprite_scene,GL_scenecam_piv
	EntityOrder GL_sprite_scene,-5
	EntityFX GL_sprite_scene,1
	
	
	
	CameraViewportX	= viewportx
	CameraViewportY	= viewporty
	CameraViewportW	= viewportW
	CameraViewportH	= viewportH
	
	
End Function



;Renders the filter
;--------------------------

;fade - initial fade value for the scene
;dark_passes - how many multiply passes (darkening)
;glow_passes - how many blur / glow passes
;glare_size - blur radius

;just play with the values until you find a good combination.


;IMPORTANT NOTES
;* 		The scene camera will be hidden automatically...
;		but you must hide every other camera you might be using Before calling this Function.

;*		Also if you want to turn the blurring off you MUST HIDE the GL_sprite_scene object and stop calling this function (obviously :)

;* 		The more dark_passes and glow_passes - the slower the function is ... so beware.
;		Also switching to smaller texture size will boost performance.

Function RenderGlow(fade#=.2,dark_passes=2,glow_passes=3,glare_size#=4)
	;RENDER THE SCENE WITH MAIN CAMERA AND COPY TO TEXTURE
	;Also apply the fade filter
	GL_camzoom# = cc_cam_realzoom
	If bloom_mbmode = 1 Then
	;	TextureBlend GL_tex2,4
		ScaleSprite GL_sprite,2*(1.0/GL_camzoom),2*(1.0/GL_camzoom)
	;	EntityOrder gl_sprite,-100
	Else
		TextureBlend GL_tex2, 2
		ScaleSprite GL_sprite,2.1*(1.0/GL_camzoom),2.1*(1.0/GL_camzoom)
		EntityOrder gl_sprite,-5.1
	EndIf
	If bloom_effect2 > 0.01 Then
		HideEntity GL_sprite_scene
		EntityBlend GL_sprite,bloom_mbmode
		EntityColor GL_sprite,168,168,168
		If bloom_mbmode = 2 Then EntityColor GL_sprite,253,253,253
		EntityAlpha GL_sprite,bloom_effect2
	Else
		HideEntity GL_sprite_scene
		EntityBlend GL_sprite,bloom_mbmode
		EntityColor GL_sprite,0,0,0
		EntityAlpha GL_sprite,bloom_effect2
	EndIf
	
	ScaleSprite GL_sprite_scene,2*(1.0/GL_camzoom),2*(1.0/GL_camzoom)
	;If bloom_mbmode = 1 Then HideEntity gl_sprite

	CameraViewport GL_scenecam,0,0,GL_texsize,GL_texsize
	RenderWorld
	CopyRect 1,1,GL_texsize-1,GL_texsize-1,0,0,BackBuffer(),GL_texbuffer2
	CameraViewport GL_scenecam, 0,0,main_width,main_height;CameraViewportX, CameraViewportY, CameraViewportW, CameraViewportH
	
	HideEntity gl_sprite
	ShowEntity GL_sprite_scene
	EntityColor GL_sprite_scene,255,255,255
	EntityAlpha GL_sprite_scene,1
	
	
	;CopyRect 1,1,GL_texsize-1,GL_texsize-1,0,0,GL_texbuffer2,GL_texbuffer
	
	;SWITCH TO GLOW CAMERA AND HIDE MAIN CAMERA
	HideEntity GL_scenecam
	ShowEntity GL_cam
	
	
	;MULTIPLY BLEND WITH BACKBUFFER TO MAKE THE CONTRAST HIGHER
	;(RESERVE THE LUMINATED AREAS)
		ShowEntity GL_Sprited
		EntityBlend GL_sprited,2
		EntityColor GL_Sprited,128,128,128
		For i=1 To dark_passes
		RenderWorld 
		Next
		CopyRect 1,0,GL_texsize-1,GL_texsize,0,0,BackBuffer(),GL_texbuffer
		EntityBlend GL_sprited,1
		HideEntity GL_sprited
	
	
	
	;BLUR And PUT SOME GLOW ON THE TEXTURE
	;MORE PASSES - MORE GLOW
	HideEntity GL_cam
	EntityColor GL_sprite_SCENE,128,128,128
	For i=1 To glow_passes
	blurtexture(GL_tex,1,glare_size)
	EntityColor GL_sprite_SCENE,255,255,255
	Next
	ScaleTexture GL_tex, 1,1
	PositionTexture GL_tex,0,0
	TextureBlend GL_tex, 2
	
	If bloom_mbmode = 1
		HideEntity GL_cam
		EntityColor GL_sprite,253,253,253
		;TextureBlend GL_tex2, 4
		For i=1 To 1
		blurtexture(GL_tex2,2,3,1)
		;EntityColor GL_sprite,255,255,255
		Next
		ScaleTexture GL_tex2, 1,1
		PositionTexture GL_tex2,0,0
		;TextureBlend GL_tex2, 4
	EndIf
	
	;SWITCH BACK TO NORMAL CAMERA
	ShowEntity GL_scenecam
	
	;BLEND WITH ADDITIVE BLENDING WITH THE SCENE
	EntityBlend GL_sprite_scene,3
	
	If bloom_pause = 0
		bs# = (1-bloom_effect2)*bloom_effect*main_bloom
		EntityAlpha GL_sprite_scene,bs
		bloom_effect2 = (bloom_effect2*.9 + bloom_mb*.1)
		EntityAlpha GL_sprite, bloom_effect2*bloom_effect*2*main_bloom
		EntityBlend GL_sprite, bloom_mbmode
		ShowEntity gl_sprite
	Else
		HideEntity gl_sprite
		HideEntity gl_sprite_scene
	EndIf
End Function

Function RenderBlur(fade#=0.15,dark_passes=2,glow_passes=3,glare_size#=4)

	HideEntity GL_sprite
	EntityBlend GL_sprite_scene,1
	EntityColor GL_sprite_scene,0,0,0
	EntityAlpha GL_sprite_scene,fade
	
	
	
	CameraViewport GL_scenecam,0,0,GL_texsize,GL_texsize
	RenderWorld
	CopyRect 1,1,GL_texsize-1,GL_texsize-1,0,0,BackBuffer(),GL_texbuffer
	CameraViewport GL_scenecam, 0,0,main_width,main_height;CameraViewportX, CameraViewportY, CameraViewportW, CameraViewportH
	
	ShowEntity GL_sprite
	EntityColor GL_sprite_scene,255,255,255
	EntityAlpha GL_sprite_scene,1
	
	
	;SWITCH TO GLOW CAMERA AND HIDE MAIN CAMERA
	HideEntity GL_scenecam
	ShowEntity GL_cam
	
	
	;MULTIPLY BLEND WITH BACKBUFFER TO MAKE THE CONTRAST HIGHER
	;(RESERVE THE LUMINATED AREAS)
	EntityBlend GL_sprite,2
	For i=1 To dark_passes
	RenderWorld 
	Next
	CopyRect 1,0,GL_texsize-1,GL_texsize,0,0,BackBuffer(),GL_texbuffer
	EntityBlend GL_sprite,1
	HideEntity GL_sprite
	
	
	;BLUR AND PUT SOME GLOW ON THE TEXTURE
	;MORE PASSES - MORE GLOW
	HideEntity GL_cam
	For i=1 To glow_passes
	blurtexture(GL_tex,1,glare_size)
	Next
	ScaleTexture GL_tex, 1,1
	PositionTexture GL_tex,0,0
	TextureBlend GL_tex, 2
	
	;SWITCH BACK TO NORMAL CAMERA
	ShowEntity GL_scenecam
	
	;BLEND WITH ADDITIVE BLENDING WITH THE SCENE
	EntityBlend GL_sprite_scene,3
	
	
	EntityAlpha GL_sprite_scene, .3
	EntityAlpha GL_sprite,.3

End Function

Function RenderEntityGlow2( ENTITY,		fade#=0.15,dark_passes=2,glow_passes=3,glare_size#=4,blurqual=1)
;RENDER THE SCENE WITH MAIN CAMERA AND COPY TO TEXTURE
;Also apply the fade filter

oldx# = EntityX( entity,1 )
oldy# = EntityY( entity,1 )
oldz# = EntityZ( entity,1 )

oldCx#= EntityX( GL_cam,1 )
oldCy#= EntityY( GL_cam,1 )
oldCz#= EntityZ( GL_cam,1 )

oldC2x#=EntityX( GL_scenecam,1 )
oldC2y#=EntityY( GL_scenecam,1 )
oldC2z#=EntityZ( GL_scenecam,1 )


PositionEntity entity, BLUR_CAM_X + oldX,BLUR_CAM_Y + oldY,BLUR_CAM_Z + oldZ,1
PositionEntity GL_cam, BLUR_CAM_X + oldCx,BLUR_CAM_Y + oldCy,BLUR_CAM_Z + oldCz,1
PositionEntity GL_scenecam, BLUR_CAM_X + oldC2x,BLUR_CAM_Y + oldC2y,BLUR_CAM_Z + oldC2z,1

;DebugLog "Entity:	X = "+EntityX(entity)+" Y = "+EntityY(entity)+" Z = "+EntityZ(entity)
;DebugLog "Cam:		X = "+EntityX(GL_cam)+" Y = "+EntityY(GL_cam)+" Z = "+EntityZ(GL_cam)
;DebugLog "SceneCam:	X = "+EntityX(GL_scenecam)+" Y = "+EntityY(GL_scenecam)+" Z = "+EntityZ(GL_scenecam)
;
;PositionEntity GL_scenecam, GL_scenecam + EntityX(GL_cam),BLUR_CAM_Y + EntityY(GL_cam),BLUR_CAM_Z + EntityZ(GL_cam)



GL_renderingtimer = GL_renderingtimer +1
If MilliSecs() - GL_renderingtimer > 20 Then
	GL_renderingtimer = MilliSecs()
Else
	Return
EndIf


HideEntity GL_sprite
EntityBlend GL_sprite_scene,1
EntityColor GL_sprite_scene,0,0,0
EntityAlpha GL_sprite_scene,fade

;ScaleSprite GL_sprite_scene,2*(1.0/GL_camzoom),2*(1.0/GL_camzoom)


CameraViewport GL_scenecam,0,0,GL_texsize,GL_texsize
RenderWorld
CopyRect 1,1,GL_texsize-1,GL_texsize-1,0,0,BackBuffer(),GL_texbuffer
CameraViewport GL_scenecam,0,26,main_width,main_Height-26

ShowEntity GL_sprite
;EntityColor GL_sprite_scene,255,255,255
;EntityAlpha GL_sprite_scene,1


;SWITCH TO GLOW CAMERA AND HIDE MAIN CAMERA
	HideEntity GL_scenecam
	ShowEntity GL_cam


;MULTIPLY BLEND WITH BACKBUFFER TO MAKE THE CONTRAST HIGHER
;(RESERVE THE LUMINATED AREAS)
EntityBlend GL_sprite,2		
For i=1 To dark_passes
	RenderWorld 
Next
CopyRect 1,0,GL_texsize-1,GL_texsize,0,0,BackBuffer(),GL_texbuffer
EntityBlend GL_sprite,1
HideEntity GL_sprite


;BLUR AND PUT SOME GLOW ON THE TEXTURE
;MORE PASSES - MORE GLOW
HideEntity GL_cam
For i=1 To glow_passes
	blurtexture(GL_tex,1,glare_size)
Next
ScaleTexture GL_tex, 1,1
PositionTexture GL_tex,0,0
TextureBlend GL_tex, 2

;SWITCH BACK TO NORMAL CAMERA
ShowEntity GL_scenecam

;BLEND WITH ADDITIVE BLENDING WITH THE SCENE
EntityBlend GL_sprite_scene,3

PositionEntity entity, oldx,oldy,oldz,1
PositionEntity GL_cam, oldCx,oldCy,oldCz,1
PositionEntity GL_scenecam, oldC2x,oldC2y,oldC2z,1


End Function

Function Glow_Clear()

	FreeEntity GL_sprite_scene
	FreeEntity GL_sprite
	FreeEntity GL_cam
	
End Function



Function RenderEntityGlow( ENTITY,		fade#=0.15,dark_passes=2,glow_passes=3,glare_size#=4,blurqual=1)
;RENDER THE SCENE WITH MAIN CAMERA AND COPY TO TEXTURE
;Also apply the fade filter

oldx# = EntityX( entity,1 )
oldy# = EntityY( entity,1 )
oldz# = EntityZ( entity,1 )

oldCx#= EntityX( GL_cam,1 )
oldCy#= EntityY( GL_cam,1 )
oldCz#= EntityZ( GL_cam,1 )

oldC2x#=EntityX( GL_scenecam,1 )
oldC2y#=EntityY( GL_scenecam,1 )
oldC2z#=EntityZ( GL_scenecam,1 )


PositionEntity entity, BLUR_CAM_X + oldX,BLUR_CAM_Y + oldY,BLUR_CAM_Z + oldZ,1
PositionEntity GL_cam, BLUR_CAM_X + oldCx,BLUR_CAM_Y + oldCy,BLUR_CAM_Z + oldCz,1
PositionEntity GL_scenecam, BLUR_CAM_X + oldC2x,BLUR_CAM_Y + oldC2y,BLUR_CAM_Z + oldC2z,1

;DebugLog "Entity:	X = "+EntityX(entity)+" Y = "+EntityY(entity)+" Z = "+EntityZ(entity)
;DebugLog "Cam:		X = "+EntityX(GL_cam)+" Y = "+EntityY(GL_cam)+" Z = "+EntityZ(GL_cam)
;DebugLog "SceneCam:	X = "+EntityX(GL_scenecam)+" Y = "+EntityY(GL_scenecam)+" Z = "+EntityZ(GL_scenecam)
;
;PositionEntity GL_scenecam, GL_scenecam + EntityX(GL_cam),BLUR_CAM_Y + EntityY(GL_cam),BLUR_CAM_Z + EntityZ(GL_cam)



renderglow fade#,dark_passes,glow_passes,glare_size#;,blurqual


PositionEntity entity, oldx,oldy,oldz,1
PositionEntity GL_cam, oldCx,oldCy,oldCz,1
PositionEntity GL_scenecam, oldC2x,oldC2y,oldC2z,1


End Function
