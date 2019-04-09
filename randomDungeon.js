var JavaPackages = new JavaImporter(
	Packages.ray.rage.SceneManager
);
with (JavaPackages)
{
	print("randomizing dungeon");
	
	var n=3+Math.floor(Math.random() * 16);
	for(n;n>0; n--){
		dungeon.addRoom();
	}
}