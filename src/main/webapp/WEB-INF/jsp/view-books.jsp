<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
    <head>
        <title>View Games</title>
    </head>
	<style>
	.center {
	  display: flex;
	  justify-content: center;
	  align-items: center;
	  height: 700px;
	}
	
	td{
		
		padding: 3px;
	}
	</style>
	<script>
		
		setTimeout(function(){
			window.location.reload();
		}, 10000);
		
		</script>
	
    <body>

		<center>
			
			<p>When you don't open (or when you've closed) the interface;<br/>
			The bots play in "headless"-mode<br/>
		 -> they don't wait for the interface to settle<br/>
	 (so they play faster...).<br/>
 You can see some statistics of the games below,<br/>
 (and are always free to open past or present games...).</p>
 
 			<h1>${challenger} is challenging ${challengee}</h1>
			
			<p>${challenger} needs to have 5 wins, and 2 (at least) stale mates<br/>
			to claim it's victory...
			</p>

			<p>${challenger} won ${metaJudge.wins} times, lost  ${metaJudge.losses} times<br/>
				and had ${metaJudge.staleMate} stale mates/pats...</p>

				<c:choose>
				    <c:when test="${metaJudge.apt}">
						<h1>Your bot is apt to compete now!</h1>
						
						<p>If you can do this 5 times you're probably our next progidy...</p>
						
						<p>When you submit your bot there are 3 'one-shot' games to see whether your bot is apt:<br/>
						You need to have at least 5 mate's & 2 stale mates/pats 3 times in a row,<br/>
						than your bot will be added to the site bots...</p>
						
					</c:when>    
					<c:otherwise>
						<c:choose>
						    <c:when test="${metaJudge.fail}">
								<img src="/imgz/defeat.jpg" />
							</c:when>    
							<c:otherwise>
								<table class="center">
					                <c:forEach items="${books}" var="book">
					                    <tr>
											<c:choose>
											    <c:when test="${book.status != 'Not begun (yet)'}">
													<td><a href="http://localhost:8080/c44/c44.html?id=${book.greenHash}" target="_blank">${book.md5}</td>		
											    </c:when>    
											    <c:otherwise>
													<td>${book.md5}</td>
											    </c:otherwise>
											</c:choose>
					
					                        <td>${book.status}</td>
											<td><img src='/imgz/${book.image}' width="50" height="50" /></td>
											<td>${book.nbOfMovez} moves played</td>
											<td>${book.result}</td>
					                    </tr>
					                </c:forEach>
									</table>
						    </c:otherwise>
						</c:choose>
					</c:otherwise>
				</c:choose>

				</center>
    </body>
</html>