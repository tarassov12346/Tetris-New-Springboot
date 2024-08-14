<%@ page import="com.app.game.tetris.serviceImpl.State" %>
<%@ page import="com.app.game.tetris.serviceImpl.Stage" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html>
<head>
    <title>Tetris</title>

    <link href="/css/styleHello.css" rel="stylesheet">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

    <script src="<c:url value="/resources/jquery-3.6.0.min.js"/>"></script>
</head>
<body>

<h1 text align="center"> <b id="helloBox"> Hello ${player}! WELCOME TO RAGING HORSE TETRIS!</b> </h1>
<h2 text align="center"> <b id="bestPlayerBox"> Wanna challenge the best player ${bestPlayer}</b> </h2>
<h3 text align="center"> <b id="bestScoreBox"> with his score ${bestScore}?</b> </h3>
<img class="displayed" src="../img/black.png" alt="" width="240" height="384" >
<img class="raging" src="../img/raging.jpg" alt="" width="120" height="120">

<div id="controls">
   <button id="startGameButton" type="button" class="button" onclick="start()" >Start</button>
</div>

<script>




function start() {
 window.location='/start';
}


</script>

</body>
</html>