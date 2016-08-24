<#include "/templates/includes/header_normal.tpl">

<form action="activate" method="post">
  <p>Abbiamo inviato un SMS al numero <strong>${data.phone}</strong> contenente un codice di verifica.
        Per autorizzare il dispositivo, inserisca di seguito il codice che
        ricever&agrave;</p>

  <div class="form-group">
    <label for="code">Codice di attivazione</label>
    <input type="number" id="code" class="form-control" name="code" placeholder="Codice" required>
  </div>
  <button type="submit" class="btn btn-success">Continua</button>
</form>

<#include "/templates/includes/footer_normal.tpl">