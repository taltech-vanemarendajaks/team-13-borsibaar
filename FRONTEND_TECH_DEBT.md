# Frontend Technical Debt

---

### 1. TypeScript Type Checking Is Disabled

**File**: `frontend/next.config.ts`

```typescript
typescript: {
    ignoreBuildErrors: true,
}
```

**Problem**:

- Build succeeds even with type errors
- Runtime crashes may occur unexpectedly
- No type safety guarantees

**Impact**:

- **Found**: 12 instances of `@ts-ignore`, `@ts-expect-error` or `any`
- 37 uses of `console.log/error/warn` (debug code in production)

**Solution**:

1. Remove `ignoreBuildErrors: true`
2. Fix all type errors one by one
3. Add strict type definitions to all components

---

### 2. Poor Folder Structure & Component Organization

**Current Structure**:

```
frontend/
├── app/
│   ├── (protected)/
│   │   ├── (sidebar)/
│   │   │   ├── inventory/
│   │   │   │   └── page.tsx          # ❌ 1,195 lines! Everything in one file
│   │   │   ├── dashboard/
│   │   │   │   └── page.tsx          # ❌ 425 lines
│   │   │   └── pos/
│   │   │       ├── page.tsx          # 273 lines (management)
│   │   │       ├── StationCard.tsx   # Mixed: components in route folder
│   │   │       ├── StationDialog.tsx
│   │   │       └── [stationId]/
│   │   │           ├── page.tsx      # 311 lines (POS system)
│   │   │           ├── CartSidebar.tsx    # ❌ Route-specific components
│   │   │           ├── ProductCard.tsx
│   │   │           └── POSHeader.tsx
│   │   ├── client/
│   │   │   ├── page.tsx              # 277 lines
│   │   │   └── Chart.tsx             # ❌ 460 lines! Massive component
│   │   └── onboarding/
│   │       └── page.tsx
│   └── api/backend/                  # 25 proxy routes (tech debt)
├── components/
│   ├── ui/                           # ✅ 12 Radix UI components (good!)
│   └── sidebar.tsx                   # ❌ Only 1 shared component outside ui/
├── hooks/
│   └── use-mobile.ts                 # ❌ Only 1 hook
├── lib/
│   ├── utils.ts
│   └── auth/
└── utils/
    └── constants.ts
```

**Problems**:

#### 1. **No Separation Between Routes and Components**

Currently: Components are mixed inside route folders

```bash
app/(protected)/(sidebar)/pos/
├── page.tsx                 # Route handler ✅
├── StationCard.tsx          # ❌ Should be in components/
├── StationDialog.tsx        # ❌ Should be in components/
└── [stationId]/
    ├── page.tsx             # Route handler ✅
    ├── CartSidebar.tsx      # ❌ Should be in components/pos/
    └── ProductCard.tsx      # ❌ Should be in components/pos/
```

**Issue**: Can't reuse components across routes easily

---

#### 2. **Monolithic Page Files**

| File                       | Lines | What it contains                                     |
| -------------------------- | ----- | ---------------------------------------------------- |
| `inventory/page.tsx`       | 1,195 | Data fetching, forms, modals, tables, business logic |
| `client/Chart.tsx`         | 460   | D3 chart rendering, data transformation, styling     |
| `dashboard/page.tsx`       | 425   | Multiple widgets, stats, API calls                   |
| `pos/[stationId]/page.tsx` | 311   | POS logic, cart, product list                        |

**Issue**: Pages should be ~50-100 lines (composition only)

---

#### 3. **Missing Feature-Based Organization**

Currently: Everything is route-based
Should be: Hybrid approach (routes + features)

**Example**: `inventory` feature

Current:

```
app/(protected)/(sidebar)/inventory/
└── page.tsx  # Everything in 1,195 lines
```

Recommended:

```
app/(protected)/(sidebar)/inventory/
├── page.tsx                           # ~80 lines (composition)
├── components/
│   ├── InventoryTable.tsx
│   ├── ProductForm/
│   │   ├── ProductForm.tsx
│   │   ├── ProductFormFields.tsx
│   │   └── ProductFormValidation.ts
│   ├── CategoryForm/
│   ├── StockModals/
│   │   ├── AddStockModal.tsx
│   │   ├── RemoveStockModal.tsx
│   │   └── AdjustStockModal.tsx
│   └── TransactionHistory/
├── hooks/
│   ├── useInventory.ts
│   ├── useCategories.ts
│   └── useTransactionHistory.ts
├── types.ts
└── utils.ts
```

---

#### 4. **Shared Components Are Scarce**

**Statistics**:

- **13 total** component files (including UI lib)
- **12** are Radix UI primitives (`components/ui/`)
- **Only 1** shared component (`sidebar.tsx`)

**Missing shared components**:

```
components/
├── ui/                    # ✅ Exists (Radix primitives)
├── shared/                # ❌ Missing
│   ├── ErrorBoundary/
│   ├── LoadingSpinner/
│   ├── EmptyState/
│   ├── ErrorAlert/
│   ├── ConfirmDialog/
│   └── DataTable/
├── layout/                # ❌ Missing
│   ├── PageHeader/
│   ├── PageContainer/
│   └── Card/
└── forms/                 # ❌ Missing
    ├── FormField/
    ├── FormError/
    └── FormLabel/
```

---

#### 5. **No Clear Hooks Organization**

Currently:

```
hooks/
└── use-mobile.ts          # Only 1 hook!
```

Should have:

```
hooks/
├── api/
│   ├── useInventory.ts
│   ├── useProducts.ts
│   └── useCategories.ts
├── forms/
│   └── useProductForm.ts
└── ui/
    ├── use-mobile.ts
    └── useToast.ts
```

---

#### 6. **API Routes Are Overly Granular**

25 separate API route files that just proxy to backend:

```
app/api/backend/
├── inventory/
│   ├── route.ts
│   ├── add/route.ts
│   ├── remove/route.ts
│   ├── adjust/route.ts
│   └── product/[productId]/history/route.ts
├── bar-stations/
│   ├── route.ts
│   ├── [id]/route.ts
│   └── user/route.ts
└── ... (20+ more)
```

**Better approach**: Single catch-all proxy

```
app/api/backend/[...path]/route.ts
```

---

### **Recommended Folder Structure**

```
frontend/
├── app/
│   ├── (protected)/
│   │   ├── (sidebar)/
│   │   │   ├── inventory/
│   │   │   │   ├── page.tsx                    # 50-100 lines
│   │   │   │   ├── _components/                # Route-specific components
│   │   │   │   │   ├── InventoryTable.tsx
│   │   │   │   │   ├── ProductForm.tsx
│   │   │   │   │   ├── CategoryForm.tsx
│   │   │   │   │   └── StockModals.tsx
│   │   │   │   ├── _hooks/
│   │   │   │   │   ├── useInventory.ts
│   │   │   │   │   └── useCategories.ts
│   │   │   │   └── _types.ts
│   │   │   ├── dashboard/
│   │   │   │   ├── page.tsx
│   │   │   │   └── _components/
│   │   │   │       ├── StatsCard.tsx
│   │   │   │       ├── SalesChart.tsx
│   │   │   │       └── RecentActivity.tsx
│   │   │   └── pos/
│   │   │       ├── page.tsx
│   │   │       ├── [stationId]/
│   │   │       │   ├── page.tsx
│   │   │       │   └── _components/
│   │   │       │       ├── CartSidebar.tsx
│   │   │       │       ├── ProductGrid.tsx
│   │   │       │       └── POSHeader.tsx
│   │   │       └── _components/
│   │   │           ├── StationCard.tsx
│   │   │           └── StationDialog.tsx
│   │   └── client/
│   │       ├── page.tsx
│   │       └── _components/
│   │           └── PriceChart.tsx
│   └── api/backend/
│       └── [...path]/route.ts              # Single proxy
├── components/                             # Shared across app
│   ├── ui/                                 # Primitives (Button, Input, etc)
│   ├── shared/
│   │   ├── DataTable/
│   │   ├── ErrorBoundary/
│   │   ├── LoadingSpinner/
│   │   ├── EmptyState/
│   │   └── ConfirmDialog/
│   ├── layout/
│   │   ├── PageHeader/
│   │   ├── PageContainer/
│   │   └── Sidebar/
│   └── forms/
│       ├── FormField/
│       └── FormError/
├── hooks/
│   ├── api/
│   │   ├── useApi.ts
│   │   ├── useInventory.ts
│   │   └── useProducts.ts
│   └── ui/
│       └── use-mobile.ts
├── lib/
│   ├── api-client.ts                      # Centralized fetch
│   ├── auth/
│   └── utils.ts
└── types/
    ├── api/                               # Generated from backend
    └── ui.ts
```

**Key Principles**:

- ✅ Route-specific components start with `_` prefix (Next.js ignores them for routing)
- ✅ Shared components in `components/`
- ✅ Pages are ~50-100 lines (composition only)
- ✅ Hooks are organized by domain

---

### 3. No Shared Types & Inconsistent Error Handling

**Problems**:

#### 1. **Missing Backend-Frontend Type Contract**

**Evidence**: 11 instances of `@ts-expect-error` in `inventory/page.tsx` alone:

```typescript
// frontend/app/(protected)/(sidebar)/inventory/page.tsx:222
body: JSON.stringify({
  // @ts-expect-error: types aren't imported currently from backend
  productId: selectedProduct.productId,
  quantity: parseFloat(formData.quantity),
  notes: formData.notes,
}),
```

**Issue**: Frontend manually defines types that should come from backend

**Current state**:

```typescript
// Frontend manually defines types
interface InventoryTransactionResponseDto {
  id: number;
  inventoryId: number;
  transactionType: string;
  // ...
}

// Backend has the real types
// backend/src/main/java/com/borsibaar/dto/InventoryTransactionResponseDto.java
public record InventoryTransactionResponseDto(
    Long id,
    Long inventoryId,
    String transactionType,
    // ...
) {}
```

**Consequences**:

- ❌ Type drift: Backend changes don't propagate to frontend
- ❌ Runtime errors from type mismatches
- ❌ No autocomplete for API responses
- ❌ Manual type maintenance in 7+ files

---

#### 2. **Inconsistent Error Handling Patterns**

Found **4 different error handling patterns** across the codebase:

**Pattern 1: Generic Error State (Most Common)**

```typescript
// inventory/page.tsx, pos/page.tsx, dashboard/page.tsx
const [error, setError] = useState<string | null>(null);

try {
  const response = await fetch("/api/backend/inventory");
  if (!response.ok) throw new Error("Failed to fetch inventory");
  const data = await response.json();
  setInventory(data);
} catch (err) {
  if (err instanceof Error) {
    setError(err.message); // ❌ No distinction between error types
  }
}
```

**Pattern 2: Browser Alert**

```typescript
// inventory/page.tsx:203
catch (err) {
  if (err instanceof Error) {
    alert(err.message);  // ❌ 16 instances! Bad UX
  } else {
    alert("An unknown error occurred");
  }
}
```

**Pattern 3: Console Error Only**

```typescript
// pos/page.tsx:85
catch (err) {
  console.error("Error fetching categories:", err);  // ❌ Silent failure
}
```

**Pattern 4: Conditional Error States**

```typescript
// pos/[stationId]/page.tsx:37
if (response.status === 403 || response.status === 404) {
  setError("You don't have access to this station"); // ✅ Better!
  return;
}
```

**Issues**:

- ❌ No distinction between:
  - 401 Unauthorized (redirect to login)
  - 403 Forbidden (show permission error)
  - 400 Bad Request (show validation errors)
  - 500 Server Error (show retry button)
  - Network errors (show offline message)
- ❌ Using `alert()` for errors (blocks UI, bad UX)
- ❌ Silent failures (console.error without user feedback)
- ❌ No global error boundary

---

#### 3. **No Input Validation Before API Calls**

**Example**: Creating a product allows invalid data through

```typescript
// inventory/page.tsx:151
const handleCreateProduct = async () => {
  const productResponse = await fetch("/api/backend/product", {
    method: "POST",
    body: JSON.stringify({
      name: productForm.name, // Could be empty!
      currentPrice: parseFloat(productForm.currentPrice), // Could be NaN!
      minPrice: parseFloat(productForm.minPrice),
      maxPrice: parseFloat(productForm.maxPrice), // minPrice > maxPrice?
    }),
  });

  if (!productResponse.ok) {
    const error = await productResponse.json();
    alert(error.message); // ❌ User finds out AFTER submission
  }
};
```

**Issues**:

- No client-side validation
- User submits → waits for network → gets error → has to fix
- Bad UX (should validate before submission)

---

#### 4. **No Centralized API Client**

**Current state**: 32+ fetch calls, all slightly different

```typescript
// Variant 1: With cache: "no-store"
const response = await fetch("/api/backend/inventory", {
  cache: "no-store",
});

// Variant 2: With credentials: "include"
const response = await fetch("/api/backend/inventory/add", {
  method: "POST",
  credentials: "include",
  headers: { "Content-Type": "application/json" },
  body: JSON.stringify(data),
});

// Variant 3: Minimal
const response = await fetch(`/api/backend/bar-stations/${stationId}`);
```

**Issues**:

- ❌ Duplicated error handling logic
- ❌ Inconsistent header usage
- ❌ No retry logic
- ❌ No loading state management
- ❌ Can't add global interceptors (auth token refresh, etc)

---

### 4. Performance Issues & Memory Leaks

**Problems**:

#### 1. **Hardcoded Organization ID**

**File**: `frontend/app/(protected)/client/page.tsx:47`

```typescript
const organizationId = 2; // ❌ HARDCODED!
```

**Issues**:

- Only works for organization ID = 2
- Multi-tenant system doesn't work properly
- Can't switch between organizations
- No URL-based routing for public pages

---

#### 2. **Uncontrolled Polling Without Cleanup**

Found **3 instances** of `setInterval` without proper cleanup checks:

**Example 1: Client Page (Every 15 seconds)**

```typescript
// frontend/app/(protected)/client/page.tsx:99
useEffect(() => {
  let alive = true;

  const load = async () => {
    // ... fetch data
  };

  load();
  const refreshInterval = setInterval(load, 1000 * 15);
  return () => {
    clearInterval(refreshInterval); // ✅ Cleanup exists
    alive = false; // ✅ Prevents state updates after unmount
  };
}, []);
```

**Example 2: POS Page (Every 60 seconds)**

```typescript
// frontend/app/(protected)/(sidebar)/pos/[stationId]/page.tsx:113
const refreshInterval = setInterval(fetchProducts, 1000 * 60);
return () => clearInterval(refreshInterval);
```

**Example 3: Chart Rotation (Every 5 seconds)**

```typescript
// frontend/app/(protected)/client/Chart.tsx:132
const id = setInterval(rotateOnce, 5000);
return () => clearInterval(id);
```

**Issues**:

- ❌ Multiple pages polling simultaneously (resource waste)
- ❌ No pause when tab is inactive (background polling)
- ❌ No exponential backoff on errors
- ❌ Polling continues even when data hasn't changed

**Better approach**: Use WebSockets

---

#### 3. **Zero Performance Optimizations**

**Statistics**:

- **0** components using `React.memo()`
- **16** total uses of `useCallback`/`useMemo` (mostly in Chart.tsx)
- **Most components** re-render unnecessarily

**Example**: Inventory page with 1,195 lines

```typescript
// Every state change re-renders entire component
const [inventory, setInventory] = useState([]);
const [loading, setLoading] = useState(true);
const [error, setError] = useState<string | null>(null);
const [searchTerm, setSearchTerm] = useState("");
const [showAddModal, setShowAddModal] = useState(false);
const [showRemoveModal, setShowRemoveModal] = useState(false);
// ... 11 more state variables

// Typing in search box re-renders 1,195 lines of code!
```

**Issues**:

- ❌ Large components re-render on every state change
- ❌ Child components re-render even when props don't change
- ❌ Expensive calculations re-run unnecessarily
- ❌ D3 chart re-renders completely on every change

---

#### 4. **Missing Route Protection**

**File**: `frontend/middleware.ts:66`

```typescript
export const config = {
  matcher: ["/login", "/dashboard/:path*", "/onboarding/:path*", "/pos/:path*"],
};
```

**Issues**:

- ❌ `/inventory` route is NOT in matcher (not protected!)
- ❌ `/client` route is public (intentional?) but no org selection
- ❌ Middleware only checks 4 routes
- ❌ Other protected routes can be accessed without auth

**Example unprotected routes**:

```
/inventory          ❌ Not in matcher
/client             ❌ Public but no org selection
/(protected)/*      ❌ Relies on folder name, not actual protection
```

---

#### 5. **No Error Boundaries**

**Current state**: Zero error boundaries in the codebase

**Consequences**:

- ❌ Single component error crashes entire app
- ❌ No graceful error recovery
- ❌ Poor UX (white screen of death)
- ❌ No error reporting/logging

**Example scenario**:

```typescript
// If this fails, entire app crashes
const Chart = () => {
  const data = JSON.parse(localStorage.getItem('data'));
  return <div>{/* D3 chart */}</div>;
};
```

---

#### 6. **Zero Test Coverage**

**Statistics**:

- **7** test-related files found
- **0** actual test files (`*.test.tsx`, `*.spec.tsx`)
- **5,018** lines of frontend code
- **0%** test coverage

**Issues**:

- ❌ No component tests
- ❌ No integration tests
- ❌ No E2E tests
- ❌ Can't refactor safely
- ❌ Regression bugs go unnoticed

---

### 5. Security & Data Issues

**Problems**:

#### 1. **Inconsistent Auth State Management**

**Current state**: Auth checked in middleware + every page

```typescript
// middleware.ts
const user = await fetchUser(req); // Server-side

// Every page also checks
const fetchCurrentUser = async () => {
  const response = await fetch("/api/backend/account"); // Client-side
  if (response.ok) setCurrentUser(await response.json());
};
```

**Issues**:

- ❌ Duplicate auth checks (middleware + client)
- ❌ No centralized auth state (Redux, Zustand, Context)
- ❌ Each page implements own auth checking
- ❌ User state not shared across components

---
