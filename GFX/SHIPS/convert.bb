Graphics3D 800,600,32,2
mesh = LoadMesh("ysupport.B3D")
RotateMesh mesh,0,170,0
file = WriteFile("ysupport .tris")
surfaces = CountSurfaces(mesh)
For i0 = 1 To surfaces
	s = GetSurface(mesh,i0)
	tris = CountTriangles(s)
	For i1 = 0 To tris-1
		For i2 = 0 To 2
			v = TriangleVertex(s,i1,i2)
			WriteFloat file,VertexX(s,v)
			WriteFloat file,VertexY(s,v)
			WriteFloat file,VertexZ(s,v)
		Next
	Next
Next
CloseFile file
End