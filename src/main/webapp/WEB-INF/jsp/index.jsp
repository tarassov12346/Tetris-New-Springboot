<%@ page import="com.app.game.tetris.serviceImpl.State" %>
<%@ page import="com.app.game.tetris.serviceImpl.Stage" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Tetris</title>

    <link href="/css/styleGamePage.css" rel="stylesheet">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <script src="<c:url value="/jquery-3.6.0.min.js"/>"></script>
</head>
<body>

<h1 text align="center"> <b id="tetrisSpeedBox"> TETRIS  at speed ${stepdown} </b> </h1>

<div id="header"
  <h1> </h1>
  <h2 id="gameStatusBox">  Game status: ${gameStatus} </h2>
</div>




<div id="content"

      <div class="tetris-wrapper"</div>

       <table align="center" style="background-color: #ffffff; border:1px black solid;" border="1" width="600">
         <tr>
         <td> <b> Player </b> </td>
         <td id="playerBox"> ${player}</td>
         <td> <b> Score </b> </td>
         <td id="playerScoreBox"> ${score}</td>
         </tr>
         <tr>
         <td> <b> Best Player </b></td>
         <td id="bestPlayerBox">  ${bestplayer}</td>
         <td> <b> Best Score </b> </td>
         <td id="bestPlayerScoreBox">  ${bestscore}</td>
         </tr>
         </table>

      <table id= "table" align="center" style="background-color: #ffffff; border:1px black solid;">
      	<tr>
      	<td><img src=${cells0v0}></td>
        <td><img src=${cells0v1}></td>
        <td><img src=${cells0v2}></td>
        <td><img src=${cells0v3}></td>
        <td><img src=${cells0v4}></td>
        <td><img src=${cells0v5}></td>
        <td><img src=${cells0v6}></td>
        <td><img src=${cells0v7}></td>
        <td><img src=${cells0v8}></td>
        <td><img src=${cells0v9}></td>
        <td><img src=${cells0v10}></td>
        <td><img src=${cells0v11}></td>
      	</tr>
      	<tr>
        <td><img src=${cells1v0}></td>
        <td><img src=${cells1v1}></td>
        <td><img src=${cells1v2}></td>
        <td><img src=${cells1v3}></td>
        <td><img src=${cells1v4}></td>
        <td><img src=${cells1v5}></td>
        <td><img src=${cells1v6}></td>
        <td><img src=${cells1v7}></td>
        <td><img src=${cells1v8}></td>
        <td><img src=${cells1v9}></td>
        <td><img src=${cells1v10}></td>
        <td><img src=${cells1v11}></td>
      	 </tr>
      	<tr>
        <td><img src=${cells2v0}></td>
        <td><img src=${cells2v1}></td>
        <td><img src=${cells2v2}></td>
        <td><img src=${cells2v3}></td>
        <td><img src=${cells2v4}></td>
        <td><img src=${cells2v5}></td>
        <td><img src=${cells2v6}></td>
        <td><img src=${cells2v7}></td>
        <td><img src=${cells2v8}></td>
        <td><img src=${cells2v9}></td>
        <td><img src=${cells2v10}></td>
        <td><img src=${cells2v11}></td>
      	</tr>
      	<tr>
      	<td><img src=${cells3v0}></td>
        <td><img src=${cells3v1}></td>
        <td><img src=${cells3v2}></td>
        <td><img src=${cells3v3}></td>
        <td><img src=${cells3v4}></td>
        <td><img src=${cells3v5}></td>
        <td><img src=${cells3v6}></td>
        <td><img src=${cells3v7}></td>
        <td><img src=${cells3v8}></td>
        <td><img src=${cells3v9}></td>
        <td><img src=${cells3v10}></td>
        <td><img src=${cells3v11}></td>
      	</tr>
      	<tr>
        <td><img src=${cells4v0}></td>
        <td><img src=${cells4v1}></td>
        <td><img src=${cells4v2}></td>
        <td><img src=${cells4v3}></td>
        <td><img src=${cells4v4}></td>
        <td><img src=${cells4v5}></td>
        <td><img src=${cells4v6}></td>
        <td><img src=${cells4v7}></td>
        <td><img src=${cells4v8}></td>
        <td><img src=${cells4v9}></td>
        <td><img src=${cells4v10}></td>
        <td><img src=${cells4v11}></td>
      	</tr>
        <tr>
       	<td><img src=${cells5v0}></td>
       	<td><img src=${cells5v1}></td>
       	<td><img src=${cells5v2}></td>
       	<td><img src=${cells5v3}></td>
       	<td><img src=${cells5v4}></td>
       	<td><img src=${cells5v5}></td>
       	<td><img src=${cells5v6}></td>
       	<td><img src=${cells5v7}></td>
       	<td><img src=${cells5v8}></td>
       	<td><img src=${cells5v9}></td>
       	<td><img src=${cells5v10}></td>
       	<td><img src=${cells5v11}></td>

        </tr>
        <tr>
        <td><img src=${cells6v0}></td>
        <td><img src=${cells6v1}></td>
        <td><img src=${cells6v2}></td>
        <td><img src=${cells6v3}></td>
        <td><img src=${cells6v4}></td>
        <td><img src=${cells6v5}></td>
        <td><img src=${cells6v6}></td>
        <td><img src=${cells6v7}></td>
        <td><img src=${cells6v8}></td>
        <td><img src=${cells6v9}></td>
        <td><img src=${cells6v10}></td>
        <td><img src=${cells6v11}></td>
        </tr>
        <tr>
        <td><img src=${cells7v0}></td>
        <td><img src=${cells7v1}></td>
        <td><img src=${cells7v2}></td>
        <td><img src=${cells7v3}></td>
        <td><img src=${cells7v4}></td>
        <td><img src=${cells7v5}></td>
        <td><img src=${cells7v6}></td>
        <td><img src=${cells7v7}></td>
        <td><img src=${cells7v8}></td>
        <td><img src=${cells7v9}></td>
        <td><img src=${cells7v10}></td>
        <td><img src=${cells7v11}></td>
        </tr>
        <tr>
        <td><img src=${cells8v0}></td>
        <td><img src=${cells8v1}></td>
        <td><img src=${cells8v2}></td>
        <td><img src=${cells8v3}></td>
        <td><img src=${cells8v4}></td>
        <td><img src=${cells8v5}></td>
        <td><img src=${cells8v6}></td>
        <td><img src=${cells8v7}></td>
        <td><img src=${cells8v8}></td>
        <td><img src=${cells8v9}></td>
        <td><img src=${cells8v10}></td>
        <td><img src=${cells8v11}></td>
        </tr>
        <tr>
        <td><img src=${cells9v0}></td>
        <td><img src=${cells9v1}></td>
        <td><img src=${cells9v2}></td>
        <td><img src=${cells9v3}></td>
        <td><img src=${cells9v4}></td>
        <td><img src=${cells9v5}></td>
        <td><img src=${cells9v6}></td>
        <td><img src=${cells9v7}></td>
        <td><img src=${cells9v8}></td>
        <td><img src=${cells9v9}></td>
        <td><img src=${cells9v10}></td>
        <td><img src=${cells9v11}></td>
        </tr>
      	<tr>
        <td><img src=${cells10v0}></td>
        <td><img src=${cells10v1}></td>
        <td><img src=${cells10v2}></td>
        <td><img src=${cells10v3}></td>
        <td><img src=${cells10v4}></td>
        <td><img src=${cells10v5}></td>
        <td><img src=${cells10v6}></td>
        <td><img src=${cells10v7}></td>
        <td><img src=${cells10v8}></td>
        <td><img src=${cells10v9}></td>
        <td><img src=${cells10v10}></td>
        <td><img src=${cells10v11}></td>
        </tr>
        <tr>
        <td><img src=${cells11v0}></td>
        <td><img src=${cells11v1}></td>
        <td><img src=${cells11v2}></td>
        <td><img src=${cells11v3}></td>
        <td><img src=${cells11v4}></td>
        <td><img src=${cells11v5}></td>
        <td><img src=${cells11v6}></td>
        <td><img src=${cells11v7}></td>
        <td><img src=${cells11v8}></td>
        <td><img src=${cells11v9}></td>
        <td><img src=${cells11v10}></td>
        <td><img src=${cells11v11}></td>
        </tr>
        <tr>
        <td><img src=${cells12v0}></td>
        <td><img src=${cells12v1}></td>
        <td><img src=${cells12v2}></td>
        <td><img src=${cells12v3}></td>
        <td><img src=${cells12v4}></td>
        <td><img src=${cells12v5}></td>
        <td><img src=${cells12v6}></td>
        <td><img src=${cells12v7}></td>
        <td><img src=${cells12v8}></td>
        <td><img src=${cells12v9}></td>
        <td><img src=${cells12v10}></td>
        <td><img src=${cells12v11}></td>
        </tr>
        <tr>
        <td><img src=${cells13v0}></td>
        <td><img src=${cells13v1}></td>
        <td><img src=${cells13v2}></td>
        <td><img src=${cells13v3}></td>
        <td><img src=${cells13v4}></td>
        <td><img src=${cells13v5}></td>
        <td><img src=${cells13v6}></td>
        <td><img src=${cells13v7}></td>
        <td><img src=${cells13v8}></td>
        <td><img src=${cells13v9}></td>
        <td><img src=${cells13v10}></td>
        <td><img src=${cells13v11}></td>
        </tr>
        <tr>
        <td><img src=${cells14v0}></td>
        <td><img src=${cells14v1}></td>
        <td><img src=${cells14v2}></td>
        <td><img src=${cells14v3}></td>
        <td><img src=${cells14v4}></td>
        <td><img src=${cells14v5}></td>
        <td><img src=${cells14v6}></td>
        <td><img src=${cells14v7}></td>
        <td><img src=${cells14v8}></td>
        <td><img src=${cells14v9}></td>
        <td><img src=${cells14v10}></td>
        <td><img src=${cells14v11}></td>
        </tr>
        <tr>
        <td><img src=${cells15v0}></td>
        <td><img src=${cells15v1}></td>
        <td><img src=${cells15v2}></td>
        <td><img src=${cells15v3}></td>
        <td><img src=${cells15v4}></td>
        <td><img src=${cells15v5}></td>
        <td><img src=${cells15v6}></td>
        <td><img src=${cells15v7}></td>
        <td><img src=${cells15v8}></td>
        <td><img src=${cells15v9}></td>
        <td><img src=${cells15v10}></td>
        <td><img src=${cells15v11}></td>
        </tr>
        <tr>
        <td><img src=${cells16v0}></td>
        <td><img src=${cells16v1}></td>
        <td><img src=${cells16v2}></td>
        <td><img src=${cells16v3}></td>
        <td><img src=${cells16v4}></td>
        <td><img src=${cells16v5}></td>
        <td><img src=${cells16v6}></td>
        <td><img src=${cells16v7}></td>
        <td><img src=${cells16v8}></td>
        <td><img src=${cells16v9}></td>
        <td><img src=${cells16v10}></td>
        <td><img src=${cells16v11}></td>
        </tr>
        <tr>
        <td><img src=${cells17v0}></td>
        <td><img src=${cells17v1}></td>
        <td><img src=${cells17v2}></td>
        <td><img src=${cells17v3}></td>
        <td><img src=${cells17v4}></td>
        <td><img src=${cells17v5}></td>
        <td><img src=${cells17v6}></td>
        <td><img src=${cells17v7}></td>
        <td><img src=${cells17v8}></td>
        <td><img src=${cells17v9}></td>
        <td><img src=${cells17v10}></td>
        <td><img src=${cells17v11}></td>
        </tr>
        <tr>
        <td><img src=${cells18v0}></td>
        <td><img src=${cells18v1}></td>
        <td><img src=${cells18v2}></td>
        <td><img src=${cells18v3}></td>
        <td><img src=${cells18v4}></td>
        <td><img src=${cells18v5}></td>
        <td><img src=${cells18v6}></td>
        <td><img src=${cells18v7}></td>
        <td><img src=${cells18v8}></td>
        <td><img src=${cells18v9}></td>
        <td><img src=${cells18v10}></td>
        <td><img src=${cells18v11}></td>
        </tr>
        <tr>
        <td><img src=${cells19v0}></td>
        <td><img src=${cells19v1}></td>
        <td><img src=${cells19v2}></td>
        <td><img src=${cells19v3}></td>
        <td><img src=${cells19v4}></td>
        <td><img src=${cells19v5}></td>
        <td><img src=${cells19v6}></td>
        <td><img src=${cells19v7}></td>
        <td><img src=${cells19v8}></td>
        <td><img src=${cells19v9}></td>
        <td><img src=${cells19v10}></td>
        <td><img src=${cells19v11}></td>
         </tr>
     </table>
</div>

<div id="controls">
   <button id="leftButton" onclick="left()" >Left</button>
   <button id="rotateButton" onclick="rotate()" >Rotate</button>
   <button id="dropButton" onclick="drop()" >Drop</button>
   <button id="newGameButton" onclick="newgame()" >NewGame</button>
   <button id="saveButton" onclick="save()" >Save</button>
   <button id="restartButton" onclick="restart()" >Restart</button>
   <button id="rightButton" onclick="right()" >Right</button>
</div>



<script>

document.onkeydown = function(e){
	    if(e.keyCode == 37){
	    left()
	    }
	    if(e.keyCode == 39){
        right()
        }
        if(e.keyCode == 40){
        drop()
        }
        if(e.keyCode == 38){
        rotate()
        }
}



function rotate() {
 window.location='/1';
}

function left() {
window.location='/2';
}

function right() {
 window.location='/3';
}

function drop() {
window.location='/4';
}

function newgame() {
 window.location='/hello';
 }

 function save() {
  window.location='/save';
 }

 function restart() {
   window.location='/restart';
 }

 function record() {
   window.location='/5';
 }




function falldown() {
   if(${isGameOn}) window.location='/0';
  }




  var myTimer =setInterval(falldown, 500);
  if(!${isGameOn})  clearInterval(myTimer);

  var timerId =setTimeout(record, 3000);










</script>

</body>
</html>