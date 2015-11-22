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
<title>${article.name}目录</title>
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
					</div>
				</td>
			</tr>
		</tbody>
	</table>
	<table width="900px" align="CENTER" cellpadding="0" cellspacing="0">
		<tr>
			<td>${article.name}</td>
			<td>作者:${article.auth}</td>
			<td>分类:${article.category.name}</td>
			<td><a href="${contextPath}/view/article/download/${article.id}">下载TEXT</a></td>
		</tr>
		<tr>
			<td colspan="4"><c:forEach items="${article.parts}" var="part">
					<a
						href="${contextPath}/view/article/read/${article.id}-${part.index}">${part.name}</a>
				</c:forEach></td>
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