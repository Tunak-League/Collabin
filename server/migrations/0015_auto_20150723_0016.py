# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('server', '0014_auto_20150719_0534'),
    ]

    operations = [
        migrations.RenameField(
            model_name='projects',
            old_name='id_owner',
            new_name='owner',
        ),
        migrations.RenameField(
            model_name='skills',
            old_name='users',
            new_name='user_profiles',
        ),
        migrations.RenameField(
            model_name='types',
            old_name='users',
            new_name='user_profiles',
        ),
    ]
