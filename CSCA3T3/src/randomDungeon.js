var JavaPackages = new JavaImporter(
	Packages.ray.rage.SceneManager
);
with (JavaPackages)
{
	print("randomizing dungeon");
	
	var n=3+Math.floor(Math.random() * 16);
	var trapKey;
	for(var i=0; i<n; i++){
		dungeon.addRoom();
		trapKey=Math.floor(Math.random() * 5);	//random number 0-4
		switch(trapKey){
			case 0:
				dungeon.addTrap(i,Swinging);
				break;
			case 1:
				dungeon.addTrap(i,Spike);
				break;
			case 2:
				dungeon.addTrap(i,Pit);
				break;
		}
	}
	dungeon.finish();
}