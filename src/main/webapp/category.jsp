<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>${category.name}文章列表</title>
</head>
<body>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" />
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
		<c:forEach items="${articles}" var="article">
			<tr>
				<td><a
					href="${contextPath}/view/article/directory/${article.id}-1">${article.name}</a></td>
				<td>${article.auth}</td>
				<td>${article.category}</td>
				<td>${article.status}</td>
				<td><a
					href="${contextPath}/view/article/download/${article.id}">下载TEXT</a></td>
			</tr>
		</c:forEach>
		<tr>
			<td></td>
			<c:if test="${!empty pagenation.prev}">
				<td><a
					href="${contextPath}/view/article/category/${category.id}-${pagenation.prev}">［上一页］</a></td>
			</c:if>
			<c:if test="${empty pagenation.prev}">
				<td></td>
			</c:if>
			<td>第${pagenation.current}/${pagenation.total}页</td>
			<c:if test="${!empty pagenation.next}">
				<td><a
					href="${contextPath}/view/article/category/${category.id}-${pagenation.next}">［下一页］</a></td>
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