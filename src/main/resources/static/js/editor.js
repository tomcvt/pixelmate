const errorMsg = document.getElementById('errorMsg');
document.getElementById('runBtn').onclick = function () {
    errorMsg.textContent = '';
    fetch('/api/session/pipeline/run', { method: 'POST' })
        .then(async res => {
            if (res.ok) {
                const urls = await res.json();
                const imagesRow = document.getElementById('imagesRow');
                imagesRow.innerHTML = '';
                urls.forEach(url => {
                    const img = document.createElement('img');
                    img.src = url;
                    img.className = 'img-preview';
                    imagesRow.appendChild(img);
                });
            } else {
                let err;
                try { err = await res.json(); } catch { err = { error: 'Unknown', message: res.statusText }; }
                errorMsg.textContent = (err.error ? err.error + ': ' : '') + (err.message || JSON.stringify(err));
            }
        })
        .catch(e => {
            errorMsg.textContent = 'Network error: ' + e;
        });
};
document.getElementById('namesBtn').onclick = function () {
    errorMsg.textContent = '';
    fetch('/api/session/pipeline/operations/names')
        .then(async res => {
            if (res.ok) {
                const names = await res.json();
                const namesList = document.getElementById('namesList');
                namesList.innerHTML = '<b>Operation Names:</b> ' + names.join(', ');
            } else {
                let err;
                try { err = await res.json(); } catch { err = { error: 'Unknown', message: res.statusText }; }
                errorMsg.textContent = (err.error ? err.error + ': ' : '') + (err.message || JSON.stringify(err));
            }
        })
        .catch(e => {
            errorMsg.textContent = 'Network error: ' + e;
        });
};