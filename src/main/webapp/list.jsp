<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="${contextPath}/fmt.css" type="text/css">
<title>排行榜 文章列表</title>
</head>
<body>
	<table width="900px" align="center" cellpadding="0" cellspacing="0">
		<tbody>
			<tr>
				<td>
					<div class="Header">
						<c:forEach items="${categorys}" var="category">
							<a href="${contextPath}/view/article/category/${category.id}-1">${category.name}</a> |
						</c:forEach>
						<a href="${contextPath}/view/article/list/1">排行榜</a>
						<div class="FL">
							<form action="${contextPath}/view/article/query">
								<input type="text" size="20" maxlength="24" name="word" value=""><input
									type="submit" value="搜 索">
							</form>
						</div>
					</div>
				</td>
			</tr>
		</tbody>
	</table>
	<table width="900px" align="CENTER" cellpadding="0" cellspacing="0">
		<c:forEach items="${articles}" var="article">
			<tr class="line">
				<td><a
					href="${contextPath}/view/article/directory/${article.id}">${article.name}</a></td>
				<td>${article.auth}</td>
				<td>${article.category.name}</td>
				<td>${article.status}</td>
				<td><a
					href="${contextPath}/view/article/download/${article.id}">下载TEXT</a></td>
			</tr>
		</c:forEach>
		<tr>
			<td></td>
			<c:if test="${!empty pagenation.prev}">
				<td><a
					href="${contextPath}/view/article/list/${pagenation.prev}">［上一页］</a></td>
			</c:if>
			<c:if test="${empty pagenation.prev}">
				<td></td>
			</c:if>
			<td>第${pagenation.current}/${pagenation.total}页</td>
			<c:if test="${!empty pagenation.next}">
				<td><a
					href="${contextPath}/view/article/list/${pagenation.next}">［下一页］</a></td>
			</c:if>
			<c:if test="${empty pagenation.next}">
				<td></td>
			</c:if>
			<td></td>
		</tr>
	</table>
	<table width="90%" align="center" cellpadding="3" cellspacing="0"
		border="0">
		<tbody>
			<tr>
				<td align="center">© CopyRight 2015
					爱易读所有作品由自动化设备收集于互联网.作品各种权益与责任归原作者所有.</td>
			</tr>
		</tbody>
	</table>
</body>
</html>