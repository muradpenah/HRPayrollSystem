/**
 * HR Payroll System - Bütün API Endpointlərdən istifadə
 * DepartmentController | EmployeeController | PayrollController
 */
const API = '';
let departmentsCache = [];
let employeesCache = [];

document.addEventListener('DOMContentLoaded', () => {
    initTabs();
    loadSummary();
    loadDepartments();
    loadEmployees();
    loadPayrolls();
    fillDeptSelects();
});

function initTabs() {
    document.querySelectorAll('[data-tab]').forEach(tab => {
        tab.addEventListener('click', e => {
            e.preventDefault();
            const name = tab.dataset.tab;
            document.querySelectorAll('.tab-pane').forEach(p => p.style.display = 'none');
            document.querySelectorAll('[data-tab]').forEach(t => t.classList.remove('active'));
            const pane = document.getElementById('tab-' + name);
            if (pane) pane.style.display = 'block';
            tab.classList.add('active');
            if (name === 'departments') loadDepartments();
            if (name === 'employees') loadEmployees();
            if (name === 'payrolls') loadPayrolls();
        });
    });
    document.querySelector('[data-tab="dashboard"]').classList.add('active');
}

async function fillDeptSelects(list) {
    const depts = list || departmentsCache;
    if (!depts.length) {
        try {
            const res = await fetch(API + '/departments/getAll');
            departmentsCache = await res.json();
        } catch (e) { return; }
    } else departmentsCache = depts;
    try {
        const opts = departmentsCache.map(d => `<option value="${d.id}">${escapeHtml(d.departmentName)} (${d.id})</option>`).join('');
        ['deptStatsSelect','empDeptFilter','empDeptSalary','empTopEarnerDept','empCheckEmailsDept','empSortDept','qDeptId','qDeptIdStats'].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.innerHTML = '<option value="">Seçin</option>' + opts;
        });
        ['empCreateDeptId','empUpdateDeptId'].forEach(id => {
            const el = document.getElementById(id);
            if (el) el.innerHTML = '<option value="">departmentId</option>' + opts;
        });
    } catch (e) { console.error(e); }
}

// --- DASHBOARD ---
async function loadSummary() {
    const loading = document.getElementById('summaryLoading');
    const cards = document.getElementById('summaryCards');
    const posSection = document.getElementById('positionStatsSection');
    const posCards = document.getElementById('positionStatsCards');
    try {
        const [deptRes, empRes, payRes, posRes] = await Promise.all([
            fetch(API + '/departments/getAll'),
            fetch(API + '/employees/getALl?page=0&size=1'),
            fetch(API + '/payrolls/getALl?page=0&size=10000'),
            fetch(API + '/employees/getEmployeePositionStats')
        ]);
        const depts = await deptRes.json();
        const empPage = await empRes.json();
        const payPage = await payRes.json();
        const posStats = await posRes.json();
        document.getElementById('totalDepartments').textContent = formatNum(depts.length);
        document.getElementById('totalEmployees').textContent = formatNum(empPage.totalElements ?? 0);
        document.getElementById('totalPayrolls').textContent = formatNum(payPage.totalElements ?? 0);
        const totalNet = (payPage.content || []).reduce((s, p) => s + (p.netSalary ?? 0), 0);
        document.getElementById('totalNetPaid').textContent = formatCurr(totalNet);
        if (posStats?.length) {
            posCards.innerHTML = posStats.map(s => `<div class="col-md-6 col-lg-4"><div class="card border-0 shadow-sm"><div class="card-body"><h6 class="text-muted">${escapeHtml(s.position||'N/A')}</h6><p class="mb-0"><strong>${formatNum(s.employeeCount??0)}</strong> işçi · <strong>${formatCurr(s.totalBaseSalary??0)}</strong></p></div></div></div>`).join('');
            posSection.style.display = 'block';
        }
        loading.style.display = 'none';
        cards.style.display = 'flex';
    } catch (e) {
        loading.innerHTML = `<p class="text-danger">Xəta: ${escapeHtml(e.message)}</p>`;
    }
}

// --- DEPARTMENTS ---
async function loadDepartments() {
    const body = document.getElementById('departmentsTableBody');
    try {
        const res = await fetch(API + '/departments/getAll');
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        departmentsCache = list;
        fillDeptSelects(list);
        if (!list.length) body.innerHTML = '<tr><td colspan="5" class="text-center">Boş</td></tr>';
        else body.innerHTML = list.map(d => `
            <tr>
                <td>${d.id}</td>
                <td>${escapeHtml(d.departmentName||'-')}</td>
                <td>${escapeHtml(d.departmentAddress||'-')}</td>
                <td><span class="badge bg-${d.status==='ACTIVE'?'success':d.status==='INACTIVE'?'secondary':'danger'}">${escapeHtml(d.status||'-')}</span></td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="openDeptUpdate(${d.id})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-outline-info" onclick="openDeptStatus(${d.id})"><i class="bi bi-arrow-repeat"></i></button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteDepartment(${d.id})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        body.innerHTML = `<tr><td colspan="5" class="text-danger">${escapeHtml(e.message)}</td></tr>`;
    }
}

async function loadDeptStats() {
    const id = document.getElementById('deptStatsSelect').value;
    if (!id) return;
    const result = document.getElementById('deptStatsResult');
    try {
        const [c, t, a] = await Promise.all([
            fetch(API + `/departments/countEmployees?departmentId=${id}`),
            fetch(API + `/departments/totalMonthlyPayrollCost?departmentId=${id}`),
            fetch(API + `/departments/AverageSalary?departmentId=${id}`)
        ]);
        const count = await c.json();
        const total = await t.json();
        const avg = await a.json();
        result.innerHTML = `İşçi sayı: <strong>${count}</strong> | Ümumi maaş: <strong>${formatCurr(total??0)}</strong> | Orta maaş: <strong>${formatCurr(avg??0)}</strong>`;
    } catch (e) { result.innerHTML = '<span class="text-danger">' + e.message + '</span>'; }
}

async function createDepartment() {
    const name = document.getElementById('deptCreateName').value.trim();
    const address = document.getElementById('deptCreateAddress').value.trim();
    if (!name || !address) return alert('Ad və ünvan tələb olunur');
    try {
        const res = await fetch(API + '/departments/create', {
            method: 'POST', headers: {'Content-Type':'application/json'},
            body: JSON.stringify({ departmentName: name, departmentAddress: address })
        });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || res.status); }
        bootstrap.Modal.getInstance(document.getElementById('modalDeptCreate')).hide();
        document.getElementById('deptCreateName').value = ''; document.getElementById('deptCreateAddress').value = '';
        loadDepartments(); loadSummary();
    } catch (e) { alert(e.message); }
}

function openDeptUpdate(id) {
    const d = departmentsCache.find(x => x.id == id);
    if (!d) return;
    document.getElementById('deptUpdateId').value = d.id;
    document.getElementById('deptUpdateName').value = d.departmentName;
    document.getElementById('deptUpdateAddress').value = d.departmentAddress || '';
    new bootstrap.Modal(document.getElementById('modalDeptUpdate')).show();
}

async function updateDepartment() {
    const id = document.getElementById('deptUpdateId').value;
    const name = document.getElementById('deptUpdateName').value.trim();
    const address = document.getElementById('deptUpdateAddress').value.trim();
    if (!id || !name || !address) return alert('Bütün sahələr tələb olunur');
    try {
        const res = await fetch(API + `/departments/update/${id}`, {
            method: 'PUT', headers: {'Content-Type':'application/json'},
            body: JSON.stringify({ departmentName: name, departmentAddress: address })
        });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || res.status); }
        bootstrap.Modal.getInstance(document.getElementById('modalDeptUpdate')).hide();
        loadDepartments(); loadSummary();
    } catch (e) { alert(e.message); }
}

function openDeptStatus(id) {
    document.getElementById('deptStatusId').value = id;
    new bootstrap.Modal(document.getElementById('modalDeptStatus')).show();
}

async function updateDeptStatus() {
    const id = document.getElementById('deptStatusId').value;
    const status = document.getElementById('deptStatusSelect').value;
    if (!id) return;
    try {
        const res = await fetch(API + `/departments/${id}/status?status=${status}`, { method: 'PATCH' });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || res.status); }
        bootstrap.Modal.getInstance(document.getElementById('modalDeptStatus')).hide();
        loadDepartments(); loadSummary();
    } catch (e) { alert(e.message); }
}

async function deleteDepartment(id) {
    if (!confirm('Departamenti silmək istəyirsiniz?')) return;
    try {
        const res = await fetch(API + `/departments/delete/${id}`, { method: 'DELETE' });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || res.status); }
        loadDepartments(); loadSummary();
    } catch (e) { alert(e.message); }
}

// --- EMPLOYEES ---
const EMP_PAGE_SIZE = 10;
let empCurrentPage = 0;
let empFilterMode = 'all';

async function loadEmployees(page = 0) {
    empCurrentPage = page;
    const body = document.getElementById('employeesTableBody');
    const pag = document.getElementById('employeesPagination');
    try {
        body.innerHTML = '<tr><td colspan="9" class="text-center"><div class="spinner-border spinner-border-sm"></div></td></tr>';
        const res = await fetch(API + `/employees/getALl?page=${page}&size=${EMP_PAGE_SIZE}`);
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || res.status);
        const list = data.content || [];
        const totalPages = data.totalPages ?? 1;
        const totalEl = data.totalElements ?? 0;
        if (!list.length) body.innerHTML = '<tr><td colspan="9" class="text-center">Boş</td></tr>';
        else body.innerHTML = list.map((e, i) => `
            <tr>
                <td>${page * EMP_PAGE_SIZE + i + 1}</td>
                <td>${escapeHtml(e.fullName||'-')}</td>
                <td>${escapeHtml(e.position||'-')}</td>
                <td>${formatCurr(e.baseSalary??0)}</td>
                <td>${fmtDate(e.hireDate)}</td>
                <td>${escapeHtml(e.email||'-')}</td>
                <td>${escapeHtml(e.phoneNumber||'-')}</td>
                <td>${e.departmentId??'-'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="openEmpUpdate(${e.id})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deleteEmployee(${e.id})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `).join('');
        if (totalPages > 1) {
            pag.innerHTML = `<nav><ul class="pagination pagination-sm">
                <li class="page-item ${page===0?'disabled':''}"><a class="page-link" href="#" data-p="${page-1}">Əvvəlki</a></li>
                <li class="page-item disabled"><span class="page-link">${page+1} / ${totalPages} (${formatNum(totalEl)})</span></li>
                <li class="page-item ${page>=totalPages-1?'disabled':''}"><a class="page-link" href="#" data-p="${page+1}">Növbəti</a></li>
            </ul></nav>`;
            pag.querySelectorAll('[data-p]').forEach(b => b.onclick = e => { e.preventDefault(); if (!b.closest('.page-item').classList.contains('disabled')) loadEmployees(+b.dataset.p); });
        } else pag.innerHTML = '';
    } catch (e) {
        body.innerHTML = `<tr><td colspan="9" class="text-danger">${escapeHtml(e.message)}</td></tr>`;
    }
}

async function loadEmployeesBySearch() {
    const kw = document.getElementById('empSearchKeyword').value.trim();
    if (!kw) return loadEmployees();
    const body = document.getElementById('employeesTableBody');
    try {
        const res = await fetch(API + `/employees/searchByKeyword?keyword=${encodeURIComponent(kw)}`);
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        renderEmployeeList(body, list);
    } catch (e) { body.innerHTML = `<tr><td colspan="9" class="text-danger">${e.message}</td></tr>`; }
}

async function loadEmployeesByDept() {
    const id = document.getElementById('empDeptFilter').value;
    if (!id) return loadEmployees();
    const body = document.getElementById('employeesTableBody');
    try {
        const res = await fetch(API + `/employees/getDepartment/${id}`);
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        renderEmployeeList(body, list);
    } catch (e) { body.innerHTML = `<tr><td colspan="9" class="text-danger">${e.message}</td></tr>`; }
}

async function loadEmployeesFilterBySalary() {
    const deptId = document.getElementById('empDeptSalary').value;
    const min = document.getElementById('empMinSalary').value;
    const max = document.getElementById('empMaxSalary').value;
    if (!deptId || min==='' || max==='') return alert('Departament, min və max maaş tələb olunur');
    const body = document.getElementById('employeesTableBody');
    try {
        const res = await fetch(API + `/employees/filterBySalary?departmentId=${deptId}&minSalary=${min}&maxSalary=${max}`);
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        renderEmployeeList(body, list);
    } catch (e) { body.innerHTML = `<tr><td colspan="9" class="text-danger">${e.message}</td></tr>`; }
}

async function loadTopEarners() {
    const id = document.getElementById('empTopEarnerDept').value;
    if (!id) return alert('Departament seçin');
    const body = document.getElementById('employeesTableBody');
    try {
        const res = await fetch(API + `/employees/topEarner?departmentId=${id}`);
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        renderEmployeeList(body, list);
    } catch (e) { body.innerHTML = `<tr><td colspan="9" class="text-danger">${e.message}</td></tr>`; }
}

async function loadCheckEmails() {
    const id = document.getElementById('empCheckEmailsDept').value;
    if (!id) return alert('Departament seçin');
    const body = document.getElementById('employeesTableBody');
    try {
        const res = await fetch(API + `/employees/checkEmails?departmentId=${id}`);
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        renderEmployeeList(body, list);
    } catch (e) { body.innerHTML = `<tr><td colspan="9" class="text-danger">${e.message}</td></tr>`; }
}

async function loadEmployeesSort(endpoint) {
    const id = document.getElementById('empSortDept').value;
    if (!id) return alert('Departament seçin');
    const body = document.getElementById('employeesTableBody');
    try {
        const res = await fetch(API + `/employees/${endpoint}?departmentId=${id}`);
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        renderEmployeeList(body, list);
    } catch (e) { body.innerHTML = `<tr><td colspan="9" class="text-danger">${e.message}</td></tr>`; }
}

async function loadFilterDateAndSalary() {
    const start = document.getElementById('empStartDate').value;
    const end = document.getElementById('empEndDate').value;
    const min = document.getElementById('empMinSalaryFilter').value;
    if (!start || !end || !min) return alert('Başlanğıc, bitmə tarixi və min maaş tələb olunur');
    const body = document.getElementById('employeesTableBody');
    try {
        const res = await fetch(API + `/employees/filterDateAndSalary?startDate=${start}&endDate=${end}&minSalary=${min}`);
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        renderEmployeeList(body, list);
    } catch (e) { body.innerHTML = `<tr><td colspan="9" class="text-danger">${e.message}</td></tr>`; }
}

async function loadFindUnpaid() {
    const month = document.getElementById('empUnpaidMonth').value;
    const year = document.getElementById('empUnpaidYear').value;
    if (!year) return alert('İl tələb olunur');
    const body = document.getElementById('employeesTableBody');
    try {
        const res = await fetch(API + `/employees/findUnpaidEmployees?month=${month}&year=${year}`);
        const list = await res.json();
        if (!res.ok) throw new Error(list.message || res.status);
        renderEmployeeList(body, list);
    } catch (e) { body.innerHTML = `<tr><td colspan="9" class="text-danger">${e.message}</td></tr>`; }
}

function renderEmployeeList(body, list) {
    document.getElementById('employeesPagination').innerHTML = '';
    if (!list.length) body.innerHTML = '<tr><td colspan="9" class="text-center">Nəticə yoxdur</td></tr>';
    else body.innerHTML = list.map((e, i) => `
        <tr>
            <td>${i+1}</td>
            <td>${escapeHtml(e.fullName||'-')}</td>
            <td>${escapeHtml(e.position||'-')}</td>
            <td>${formatCurr(e.baseSalary??0)}</td>
            <td>${fmtDate(e.hireDate)}</td>
            <td>${escapeHtml(e.email||'-')}</td>
            <td>${escapeHtml(e.phoneNumber||'-')}</td>
            <td>${e.departmentId??'-'}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="openEmpUpdate(${e.id})"><i class="bi bi-pencil"></i></button>
                <button class="btn btn-sm btn-outline-danger" onclick="deleteEmployee(${e.id})"><i class="bi bi-trash"></i></button>
            </td>
        </tr>
    `).join('');
}

async function createEmployee() {
    const data = {
        fullName: document.getElementById('empCreateFullName').value.trim(),
        position: document.getElementById('empCreatePosition').value.trim(),
        hireDate: document.getElementById('empCreateHireDate').value,
        baseSalary: parseFloat(document.getElementById('empCreateBaseSalary').value) || 0,
        email: document.getElementById('empCreateEmail').value.trim() || null,
        phoneNumber: document.getElementById('empCreatePhone').value.trim() || null,
        address: document.getElementById('empCreateAddress').value.trim(),
        departmentId: document.getElementById('empCreateDeptId').value ? parseInt(document.getElementById('empCreateDeptId').value) : null
    };
    if (!data.fullName || !data.position || !data.hireDate || !data.address) return alert('Zəruri sahələr doldurulmalıdır');
    try {
        const res = await fetch(API + '/employees/create', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(data) });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || JSON.stringify(err)); }
        bootstrap.Modal.getInstance(document.getElementById('modalEmpCreate')).hide();
        document.querySelectorAll('#modalEmpCreate input, #modalEmpCreate select').forEach(i => i.value = '');
        loadEmployees(); loadSummary();
    } catch (e) { alert(e.message); }
}

function openEmpUpdate(id) {
    fetch(API + `/employees/get/${id}`).then(r => r.json()).then(d => {
        document.getElementById('empUpdateId').value = d.id;
        document.getElementById('empUpdateFullName').value = d.fullName || '';
        document.getElementById('empUpdatePosition').value = d.position || '';
        document.getElementById('empUpdateHireDate').value = (d.hireDate || '').slice(0,10);
        document.getElementById('empUpdateBaseSalary').value = d.baseSalary ?? '';
        document.getElementById('empUpdateEmail').value = d.email || '';
        document.getElementById('empUpdatePhone').value = d.phoneNumber || '';
        document.getElementById('empUpdateAddress').value = d.address || '';
        document.getElementById('empUpdateDeptId').value = d.departmentId || '';
        new bootstrap.Modal(document.getElementById('modalEmpUpdate')).show();
    }).catch(e => alert(e.message));
}

async function updateEmployee() {
    const id = document.getElementById('empUpdateId').value;
    const data = {
        fullName: document.getElementById('empUpdateFullName').value.trim(),
        position: document.getElementById('empUpdatePosition').value.trim(),
        hireDate: document.getElementById('empUpdateHireDate').value,
        baseSalary: parseFloat(document.getElementById('empUpdateBaseSalary').value) || 0,
        email: document.getElementById('empUpdateEmail').value.trim() || null,
        phoneNumber: document.getElementById('empUpdatePhone').value.trim() || null,
        address: document.getElementById('empUpdateAddress').value.trim(),
        departmentId: document.getElementById('empUpdateDeptId').value ? parseInt(document.getElementById('empUpdateDeptId').value) : null
    };
    if (!id || !data.fullName || !data.position || !data.hireDate || !data.address) return alert('Zəruri sahələr doldurulmalıdır');
    try {
        const res = await fetch(API + `/employees/update/${id}`, { method: 'PUT', headers: {'Content-Type':'application/json'}, body: JSON.stringify(data) });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || JSON.stringify(err)); }
        bootstrap.Modal.getInstance(document.getElementById('modalEmpUpdate')).hide();
        loadEmployees(); loadSummary();
    } catch (e) { alert(e.message); }
}

async function deleteEmployee(id) {
    if (!confirm('İşçini silmək istəyirsiniz?')) return;
    try {
        const res = await fetch(API + `/employees/delete/${id}`, { method: 'DELETE' });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || res.status); }
        loadEmployees(); loadSummary();
    } catch (e) { alert(e.message); }
}

// --- PAYROLLS ---
let payCurrentPage = 0;
async function loadPayrolls(page = 0) {
    const body = document.getElementById('payrollsTableBody');
    const pag = document.getElementById('payrollsPagination');
    try {
        body.innerHTML = '<tr><td colspan="8" class="text-center"><div class="spinner-border spinner-border-sm"></div></td></tr>';
        const res = await fetch(API + `/payrolls/getALl?page=${page}&size=20`);
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || res.status);
        const list = data.content || [];
        const totalPages = data.totalPages ?? 1;
        if (!list.length) body.innerHTML = '<tr><td colspan="8" class="text-center">Boş</td></tr>';
        else body.innerHTML = list.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${fmtDate(p.paymentDate)}</td>
                <td>${fmtMonth(p.paymentMonth)}</td>
                <td>${formatCurr(p.bonusAmount??0)}</td>
                <td>${formatCurr(p.taxAmount??0)}</td>
                <td>${formatCurr(p.netSalary??0)}</td>
                <td>${p.employeeId??'-'}</td>
                <td>
                    <button class="btn btn-sm btn-outline-primary" onclick="openPayrollUpdate(${p.id})"><i class="bi bi-pencil"></i></button>
                    <button class="btn btn-sm btn-outline-danger" onclick="deletePayroll(${p.id})"><i class="bi bi-trash"></i></button>
                </td>
            </tr>
        `).join('');
        if (totalPages > 1) {
            pag.innerHTML = `<nav><ul class="pagination pagination-sm">
                <li class="page-item ${page===0?'disabled':''}"><a class="page-link" href="#" data-p="${page-1}">Əvvəlki</a></li>
                <li class="page-item disabled"><span class="page-link">${page+1} / ${totalPages}</span></li>
                <li class="page-item ${page>=totalPages-1?'disabled':''}"><a class="page-link" href="#" data-p="${page+1}">Növbəti</a></li>
            </ul></nav>`;
            pag.querySelectorAll('[data-p]').forEach(b => b.onclick = e => { e.preventDefault(); if (!b.closest('.page-item').classList.contains('disabled')) loadPayrolls(+b.dataset.p); });
        } else pag.innerHTML = '';
    } catch (e) { body.innerHTML = `<tr><td colspan="8" class="text-danger">${e.message}</td></tr>`; }
}

async function createPayroll() {
    const data = {
        paymentDate: document.getElementById('payCreatePaymentDate').value,
        bonusAmount: parseFloat(document.getElementById('payCreateBonusAmount').value) || 0,
        employeeId: document.getElementById('payCreateEmployeeId').value ? parseInt(document.getElementById('payCreateEmployeeId').value) : null
    };
    if (!data.paymentDate || !data.employeeId) return alert('Tarix və işçi ID tələb olunur');
    try {
        const res = await fetch(API + '/payrolls/create', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify(data) });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || JSON.stringify(err)); }
        bootstrap.Modal.getInstance(document.getElementById('modalPayrollCreate')).hide();
        document.getElementById('payCreatePaymentDate').value = ''; document.getElementById('payCreateBonusAmount').value = ''; document.getElementById('payCreateEmployeeId').value = '';
        loadPayrolls(); loadSummary();
    } catch (e) { alert(e.message); }
}

function openPayrollUpdate(id) {
    fetch(API + `/payrolls/get/${id}`).then(r => r.json()).then(d => {
        document.getElementById('payUpdateId').value = d.id;
        document.getElementById('payUpdatePaymentDate').value = (d.paymentDate || '').slice(0,10);
        document.getElementById('payUpdateBonusAmount').value = d.bonusAmount ?? 0;
        document.getElementById('payUpdateEmployeeId').value = d.employeeId ?? '';
        new bootstrap.Modal(document.getElementById('modalPayrollUpdate')).show();
    }).catch(e => alert(e.message));
}

async function updatePayroll() {
    const id = document.getElementById('payUpdateId').value;
    const data = {
        paymentDate: document.getElementById('payUpdatePaymentDate').value,
        bonusAmount: parseFloat(document.getElementById('payUpdateBonusAmount').value) || 0,
        employeeId: document.getElementById('payUpdateEmployeeId').value ? parseInt(document.getElementById('payUpdateEmployeeId').value) : null
    };
    if (!id || !data.paymentDate || !data.employeeId) return alert('Zəruri sahələr doldurulmalıdır');
    try {
        const res = await fetch(API + `/payrolls/update/${id}`, { method: 'PUT', headers: {'Content-Type':'application/json'}, body: JSON.stringify(data) });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || JSON.stringify(err)); }
        bootstrap.Modal.getInstance(document.getElementById('modalPayrollUpdate')).hide();
        loadPayrolls(); loadSummary();
    } catch (e) { alert(e.message); }
}

async function deletePayroll(id) {
    if (!confirm('Ödənişi silmək istəyirsiniz?')) return;
    try {
        const res = await fetch(API + `/payrolls/delete/${id}`, { method: 'DELETE' });
        if (!res.ok) { const err = await res.json(); throw new Error(err.message || res.status); }
        loadPayrolls(); loadSummary();
    } catch (e) { alert(e.message); }
}

// --- QUERIES TAB ---
async function loadPayrollByEmployee() {
    const id = document.getElementById('qEmpId').value;
    if (!id) return;
    const el = document.getElementById('qEmpResult');
    try {
        const res = await fetch(API + `/payrolls/getEmployee/${id}`);
        const data = await res.json();
        el.textContent = JSON.stringify(data, null, 2);
    } catch (e) { el.textContent = e.message; }
}

async function loadHistoryByEmployeeId() {
    const id = document.getElementById('qEmpId').value;
    if (!id) return;
    const el = document.getElementById('qEmpResult');
    try {
        const res = await fetch(API + `/payrolls/getHistoryByEmployeeId?employeeId=${id}`);
        const data = await res.json();
        el.textContent = JSON.stringify(data, null, 2);
    } catch (e) { el.textContent = e.message; }
}

async function loadByDepartmentAndDate() {
    const deptId = document.getElementById('qDeptId').value;
    const month = document.getElementById('qMonth').value;
    const year = document.getElementById('qYear').value;
    if (!deptId || !month || !year) return;
    const el = document.getElementById('qDeptDateResult');
    try {
        const res = await fetch(API + `/payrolls/getByDepartmentAndDate?DepartmentId=${deptId}&month=${month}&year=${year}`);
        const data = await res.json();
        el.textContent = JSON.stringify(data, null, 2);
    } catch (e) { el.textContent = e.message; }
}

async function loadByEmployeeAndDate() {
    const empId = document.getElementById('qEmpId2').value;
    const month = document.getElementById('qMonth2').value;
    const year = document.getElementById('qYear2').value;
    if (!empId || !month || !year) return;
    const el = document.getElementById('qEmpDateResult');
    try {
        const res = await fetch(API + `/payrolls/getByEmployeeAndDate?employeeId=${empId}&month=${month}&year=${year}`);
        const data = await res.json();
        el.textContent = JSON.stringify(data, null, 2);
    } catch (e) { el.textContent = e.message; }
}

async function loadDepartmentMonthlyStats() {
    const deptId = document.getElementById('qDeptIdStats').value;
    const month = document.getElementById('qMonthStats').value;
    const year = document.getElementById('qYearStats').value;
    if (!deptId || !month || !year) return;
    const el = document.getElementById('qDeptStatsResult');
    try {
        const res = await fetch(API + `/payrolls/getDepartmentMonthlyStats?departmentId=${deptId}&month=${month}&year=${year}`);
        const data = await res.json();
        el.textContent = JSON.stringify(data, null, 2);
    } catch (e) { el.textContent = e.message; }
}

async function loadEmployeeAnnualStats() {
    const empId = document.getElementById('qEmpIdAnnual').value;
    const year = document.getElementById('qYearAnnual').value;
    if (!empId || !year) return;
    const el = document.getElementById('qAnnualResult');
    try {
        const res = await fetch(API + `/payrolls/getEmployeeAnnualStats?employeeId=${empId}&year=${year}`);
        const data = await res.json();
        el.textContent = JSON.stringify(data, null, 2);
    } catch (e) { el.textContent = e.message; }
}

// --- UTILS ---
function formatNum(n) { return new Intl.NumberFormat('az-AZ').format(n); }
function formatCurr(a) { return new Intl.NumberFormat('az-AZ',{minimumFractionDigits:2,maximumFractionDigits:2}).format(a) + ' ₼'; }
function fmtDate(s) { if (!s) return '-'; try { return new Date(s).toLocaleDateString('az-AZ'); } catch { return s; } }
function fmtMonth(m) { if (!m) return '-'; return String(m).charAt(0) + String(m).slice(1).toLowerCase(); }
function escapeHtml(t) { if (t == null) return ''; const d = document.createElement('div'); d.textContent = t; return d.innerHTML; }
