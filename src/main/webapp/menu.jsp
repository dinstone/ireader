<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<div class="sidebar-nav">
	<div class="nav-canvas">
		<div class="nav-sm nav nav-stacked"></div>
		<ul class="nav nav-pills nav-stacked main-menu">
			<li><a class="ajax-link" href="${contextPath}/view/article/list/1"><i class="glyphicon glyphicon-home"></i><span> 排行榜</span></a></li>
			<c:forEach items="${categorys}" var="category">
				<li><a class="ajax-link" href="${contextPath}/view/article/category/${category.id}-1"><i class="glyphicon glyphicon-align-justify"></i><span> ${category.name}</span></a></li>
			</c:forEach>
		</ul>
	</div>
</div>
