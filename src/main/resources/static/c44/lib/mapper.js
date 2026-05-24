
function mapToPieceAndColor(identifier, color){

	var result = "./imgz/";

	switch(color) {

	  case 0:

		result += "groen/";

		break;
	  case 1:

		result += "blauw/";

		break;
	  case 2:

		result += "rood/";

		break;
	  case 3:

		result += "geel/";

		break;
	  case 4:

		result += "dead/";

		break;
	  default:

		result += "wit2/";
	}

	switch(Array.from(identifier)[0]) {
	
	  case "p":
	  
		result += "Pion_";
		
		break;
	  case "t":
	  
		result += "Toren_";

		break;
	  case "h":
	  
		result += "Paard_";

		break;
	  case "b":
	  
		result += "Loper_";

		break;
	  case "k":
	  
		result += "Koning_";

		break;
	  case "q":
	  
		result += "Koningin_";

		break;
	  default:

		result += "wit1/";
	}
	
	switch(color) {
	
	  case 0:
	  
		result += "Groen.png";
		
		break;
	  case 1:
	  
		result += "Blauw.png";

		break;
	  case 2:
	  
		result += "Rood.png";

		break;
	  case 3:
	  
		result += "Geel.png";

		break;
	  case 4:

		result += "dead.png";

		break;
	  default:

		result += "wit2/";
	}

	return result;
}

function mapColorToBackground(color){
	
	var result = "";

	switch(color) {
	
	  case -1:
	  
		result += "";
		
		break;
	  case 0:
	  
		result += "_green";
		
		break;
	  case 1:
	  
		result += "_blue";

		break;
	  case 2:
	  
		result += "_red";

		break;
	  case 3:
	  
		result += "_yellow";

		break;
	  default:

		result += "wit3/";
	}

	return result;
}

function mapColorToX(color, x, y){

	switch(color) {
	
	  case "0":
	  
		return 11 - x; //green
		
		break;
	  case "1":
	  
		return 11 - y; //_blue

		break;
	  case "2":
	  
		return x; //_red

		break;
	  case "3":
	  
		return y; //_yellow

		break;
	}
}

function mapColorToY(color, x, y){
	
	switch(color) {
	
	  case "0":
	  
		return 11 - y; //green
		
		break;
	  case "1":
	  
		return x; //_blue

		break;
	  case "2":
	  
		return y; //_red

		break;
	  case "3":
	  
		return 11 - x; //_yellow

		break;
	}
}

function mapMoveColorToX(color, x, y){

	switch(color) {
	
	  case "0":
	  
		return 11 - x; //green
		
		break;
	  case "1":
	  
		return y; //_blue

		break;
	  case "2":
	  
		return x; //_red

		break;
	  case "3":
	  
		return 11 - y; //_yellow

		break;
	}
}

function mapMoveColorToY(color, x, y){
	
	switch(color) {
	
	  case "0":
	  
		return 11 - y; //green
		
		break;
	  case "1":
	  
		return 11 - x; //_blue

		break;
	  case "2":
	  
		return y; //_red

		break;
	  case "3":
	  
		return x; //_yellow

		break;
	}
}

function mapColorNameToColorInt(colorName){
	
	switch(colorName) {
	
	  case "GREEN":
	  
		return "0"; //green
		
		break;
	  case "BLUE":
	  
		return "1"; //_blue

		break;
	  case "RED":
	  
		return "2"; //_red

		break;
	  case "YELLOW":
	  
		return "3"; //_yellow

		break;
	}
	
	throw new Error("Wrong colorName :( !!!");
}
