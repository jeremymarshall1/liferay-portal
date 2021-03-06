/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.shopping.service.persistence;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.template.TemplateException;
import com.liferay.portal.kernel.template.TemplateManagerUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.model.ModelListener;
import com.liferay.portal.test.TransactionalTestRule;
import com.liferay.portal.test.runners.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.tools.DBUpgrader;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.test.RandomTestUtil;

import com.liferay.portlet.shopping.NoSuchCartException;
import com.liferay.portlet.shopping.model.ShoppingCart;
import com.liferay.portlet.shopping.model.impl.ShoppingCartModelImpl;
import com.liferay.portlet.shopping.service.ShoppingCartLocalServiceUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.junit.runner.RunWith;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @generated
 */
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class ShoppingCartPersistenceTest {
	@ClassRule
	public static TransactionalTestRule transactionalTestRule = new TransactionalTestRule(Propagation.REQUIRED);

	@BeforeClass
	public static void setupClass() throws TemplateException {
		try {
			DBUpgrader.upgrade();
		}
		catch (Exception e) {
			_log.error(e, e);
		}

		TemplateManagerUtil.init();
	}

	@Before
	public void setUp() {
		_modelListeners = _persistence.getListeners();

		for (ModelListener<ShoppingCart> modelListener : _modelListeners) {
			_persistence.unregisterListener(modelListener);
		}
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ShoppingCart> iterator = _shoppingCarts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}

		for (ModelListener<ShoppingCart> modelListener : _modelListeners) {
			_persistence.registerListener(modelListener);
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ShoppingCart shoppingCart = _persistence.create(pk);

		Assert.assertNotNull(shoppingCart);

		Assert.assertEquals(shoppingCart.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ShoppingCart newShoppingCart = addShoppingCart();

		_persistence.remove(newShoppingCart);

		ShoppingCart existingShoppingCart = _persistence.fetchByPrimaryKey(newShoppingCart.getPrimaryKey());

		Assert.assertNull(existingShoppingCart);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addShoppingCart();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ShoppingCart newShoppingCart = _persistence.create(pk);

		newShoppingCart.setGroupId(RandomTestUtil.nextLong());

		newShoppingCart.setCompanyId(RandomTestUtil.nextLong());

		newShoppingCart.setUserId(RandomTestUtil.nextLong());

		newShoppingCart.setUserName(RandomTestUtil.randomString());

		newShoppingCart.setCreateDate(RandomTestUtil.nextDate());

		newShoppingCart.setModifiedDate(RandomTestUtil.nextDate());

		newShoppingCart.setItemIds(RandomTestUtil.randomString());

		newShoppingCart.setCouponCodes(RandomTestUtil.randomString());

		newShoppingCart.setAltShipping(RandomTestUtil.nextInt());

		newShoppingCart.setInsure(RandomTestUtil.randomBoolean());

		_shoppingCarts.add(_persistence.update(newShoppingCart));

		ShoppingCart existingShoppingCart = _persistence.findByPrimaryKey(newShoppingCart.getPrimaryKey());

		Assert.assertEquals(existingShoppingCart.getCartId(),
			newShoppingCart.getCartId());
		Assert.assertEquals(existingShoppingCart.getGroupId(),
			newShoppingCart.getGroupId());
		Assert.assertEquals(existingShoppingCart.getCompanyId(),
			newShoppingCart.getCompanyId());
		Assert.assertEquals(existingShoppingCart.getUserId(),
			newShoppingCart.getUserId());
		Assert.assertEquals(existingShoppingCart.getUserName(),
			newShoppingCart.getUserName());
		Assert.assertEquals(Time.getShortTimestamp(
				existingShoppingCart.getCreateDate()),
			Time.getShortTimestamp(newShoppingCart.getCreateDate()));
		Assert.assertEquals(Time.getShortTimestamp(
				existingShoppingCart.getModifiedDate()),
			Time.getShortTimestamp(newShoppingCart.getModifiedDate()));
		Assert.assertEquals(existingShoppingCart.getItemIds(),
			newShoppingCart.getItemIds());
		Assert.assertEquals(existingShoppingCart.getCouponCodes(),
			newShoppingCart.getCouponCodes());
		Assert.assertEquals(existingShoppingCart.getAltShipping(),
			newShoppingCart.getAltShipping());
		Assert.assertEquals(existingShoppingCart.getInsure(),
			newShoppingCart.getInsure());
	}

	@Test
	public void testCountByGroupId() {
		try {
			_persistence.countByGroupId(RandomTestUtil.nextLong());

			_persistence.countByGroupId(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByUserId() {
		try {
			_persistence.countByUserId(RandomTestUtil.nextLong());

			_persistence.countByUserId(0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testCountByG_U() {
		try {
			_persistence.countByG_U(RandomTestUtil.nextLong(),
				RandomTestUtil.nextLong());

			_persistence.countByG_U(0L, 0L);
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ShoppingCart newShoppingCart = addShoppingCart();

		ShoppingCart existingShoppingCart = _persistence.findByPrimaryKey(newShoppingCart.getPrimaryKey());

		Assert.assertEquals(existingShoppingCart, newShoppingCart);
	}

	@Test
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		try {
			_persistence.findByPrimaryKey(pk);

			Assert.fail("Missing entity did not throw NoSuchCartException");
		}
		catch (NoSuchCartException nsee) {
		}
	}

	@Test
	public void testFindAll() throws Exception {
		try {
			_persistence.findAll(QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				getOrderByComparator());
		}
		catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	protected OrderByComparator<ShoppingCart> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create("ShoppingCart", "cartId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"itemIds", true, "couponCodes", true, "altShipping", true,
			"insure", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ShoppingCart newShoppingCart = addShoppingCart();

		ShoppingCart existingShoppingCart = _persistence.fetchByPrimaryKey(newShoppingCart.getPrimaryKey());

		Assert.assertEquals(existingShoppingCart, newShoppingCart);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ShoppingCart missingShoppingCart = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingShoppingCart);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {
		ShoppingCart newShoppingCart1 = addShoppingCart();
		ShoppingCart newShoppingCart2 = addShoppingCart();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newShoppingCart1.getPrimaryKey());
		primaryKeys.add(newShoppingCart2.getPrimaryKey());

		Map<Serializable, ShoppingCart> shoppingCarts = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, shoppingCarts.size());
		Assert.assertEquals(newShoppingCart1,
			shoppingCarts.get(newShoppingCart1.getPrimaryKey()));
		Assert.assertEquals(newShoppingCart2,
			shoppingCarts.get(newShoppingCart2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {
		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ShoppingCart> shoppingCarts = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(shoppingCarts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {
		ShoppingCart newShoppingCart = addShoppingCart();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newShoppingCart.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ShoppingCart> shoppingCarts = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, shoppingCarts.size());
		Assert.assertEquals(newShoppingCart,
			shoppingCarts.get(newShoppingCart.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys()
		throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ShoppingCart> shoppingCarts = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(shoppingCarts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey()
		throws Exception {
		ShoppingCart newShoppingCart = addShoppingCart();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newShoppingCart.getPrimaryKey());

		Map<Serializable, ShoppingCart> shoppingCarts = _persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, shoppingCarts.size());
		Assert.assertEquals(newShoppingCart,
			shoppingCarts.get(newShoppingCart.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery = ShoppingCartLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(new ActionableDynamicQuery.PerformActionMethod() {
				@Override
				public void performAction(Object object) {
					ShoppingCart shoppingCart = (ShoppingCart)object;

					Assert.assertNotNull(shoppingCart);

					count.increment();
				}
			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting()
		throws Exception {
		ShoppingCart newShoppingCart = addShoppingCart();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingCart.class,
				ShoppingCart.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("cartId",
				newShoppingCart.getCartId()));

		List<ShoppingCart> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		ShoppingCart existingShoppingCart = result.get(0);

		Assert.assertEquals(existingShoppingCart, newShoppingCart);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingCart.class,
				ShoppingCart.class.getClassLoader());

		dynamicQuery.add(RestrictionsFactoryUtil.eq("cartId",
				RandomTestUtil.nextLong()));

		List<ShoppingCart> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting()
		throws Exception {
		ShoppingCart newShoppingCart = addShoppingCart();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingCart.class,
				ShoppingCart.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("cartId"));

		Object newCartId = newShoppingCart.getCartId();

		dynamicQuery.add(RestrictionsFactoryUtil.in("cartId",
				new Object[] { newCartId }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCartId = result.get(0);

		Assert.assertEquals(existingCartId, newCartId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(ShoppingCart.class,
				ShoppingCart.class.getClassLoader());

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("cartId"));

		dynamicQuery.add(RestrictionsFactoryUtil.in("cartId",
				new Object[] { RandomTestUtil.nextLong() }));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		if (!PropsValues.HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE) {
			return;
		}

		ShoppingCart newShoppingCart = addShoppingCart();

		_persistence.clearCache();

		ShoppingCartModelImpl existingShoppingCartModelImpl = (ShoppingCartModelImpl)_persistence.findByPrimaryKey(newShoppingCart.getPrimaryKey());

		Assert.assertEquals(existingShoppingCartModelImpl.getGroupId(),
			existingShoppingCartModelImpl.getOriginalGroupId());
		Assert.assertEquals(existingShoppingCartModelImpl.getUserId(),
			existingShoppingCartModelImpl.getOriginalUserId());
	}

	protected ShoppingCart addShoppingCart() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ShoppingCart shoppingCart = _persistence.create(pk);

		shoppingCart.setGroupId(RandomTestUtil.nextLong());

		shoppingCart.setCompanyId(RandomTestUtil.nextLong());

		shoppingCart.setUserId(RandomTestUtil.nextLong());

		shoppingCart.setUserName(RandomTestUtil.randomString());

		shoppingCart.setCreateDate(RandomTestUtil.nextDate());

		shoppingCart.setModifiedDate(RandomTestUtil.nextDate());

		shoppingCart.setItemIds(RandomTestUtil.randomString());

		shoppingCart.setCouponCodes(RandomTestUtil.randomString());

		shoppingCart.setAltShipping(RandomTestUtil.nextInt());

		shoppingCart.setInsure(RandomTestUtil.randomBoolean());

		_shoppingCarts.add(_persistence.update(shoppingCart));

		return shoppingCart;
	}

	private static Log _log = LogFactoryUtil.getLog(ShoppingCartPersistenceTest.class);
	private List<ShoppingCart> _shoppingCarts = new ArrayList<ShoppingCart>();
	private ModelListener<ShoppingCart>[] _modelListeners;
	private ShoppingCartPersistence _persistence = ShoppingCartUtil.getPersistence();
}