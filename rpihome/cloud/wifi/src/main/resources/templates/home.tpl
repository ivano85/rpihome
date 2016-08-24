<#include "/templates/includes/header_normal.tpl">

<form action="send-code" method="post">
  <p>Innanzi tutto dovr&agrave; leggere ed accettare i
     <a href="">Termini e condizioni d'uso del servizio</a> e
     l'<a href="">informativa sulla privacy</a>.</p>
  <div class="checkbox">
    <label>
      <input type="checkbox" name="accept-terms" value="true" required> Accetto
    </label>
  </div>
  
  <p>Ora compili i campi sottostanti.</p>

  <div class="form-group">
    <label for="prefix">Prefisso</label>
    <select id="prefix" class="form-control" name="prefix" required>
        <option value="39">Italia (+39)</option>
    </select>
  </div>
  <div class="form-group">
    <label for="phone">Numero di telefono</label>
    <input type="number" id="phone" class="form-control" name="phone" placeholder="Numero" required>
  </div>
  <button type="submit" class="btn btn-success">Continua</button>
</form>

<#include "/templates/includes/footer_normal.tpl">